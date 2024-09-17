package com.wellfactored.api.northflank
package client.http4s

import endpoints.ServiceEndpoints.ServiceResults
import endpoints.{NorthflankError, PaginatedResults, Pagination, PaginationInput, PaginationParams, ServiceEndpoints}
import model.{ProjectId, Service, ServiceId}

import cats.effect.Async
import cats.syntax.all.*
import fs2.Stream
import org.http4s.Uri
import org.http4s.client.Client
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.{Endpoint, auth, oneOf}

class ServicesClient[F[_]: Async](bearerToken: String, projectId: ProjectId, baseUrl: Uri)(httpClient: Client[F]) {
  private val interpreter: Http4sClientInterpreter[F] = Http4sClientInterpreter()

  private val authedGetServices: Endpoint[String, PaginationInput[ProjectId], NorthflankError, PaginatedResults[ServiceResults], Any] =
    ServiceEndpoints.getServices
      .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))
      .securityIn(auth.bearer[String]())

  def getServices: F[Either[String, List[Service.Summary]]] =
    interpreter.toSecureRequest(authedGetServices, Some(baseUrl))(bearerToken)(PaginationInput(projectId)) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data.services)
        }
    }

  def getServicesStream: Stream[F, Service.Summary] =
    Stream
      .unfoldLoopEval(PaginationParams(None, None, None)) { paginationParams =>
        interpreter.toSecureRequest(authedGetServices, Some(baseUrl))(bearerToken)(PaginationInput(projectId, paginationParams)) match {
          case (request, handler) =>
            httpClient
              .run(request)
              .use(handler)
              // Any error in the http request will manifest as an exception in the stream
              .map(response => handleResponse(request, response).toOption.get)
              .map { result =>
                println(result.pagination)
                val nextPageParams: Option[PaginationParams] = result.pagination match {
                  case Some(Pagination(true, Some(cursor), _)) => Some(PaginationParams(None, None, Some(cursor)))
                  case _                                       => None
                }
                (result.data.services, nextPageParams)
              }
        }
      }
      .flatMap(Stream.emits)

  def getService(id: ServiceId): F[Either[String, Service.Detail]] = {
    val e = ServiceEndpoints.getService
      .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))
      .securityIn(auth.bearer[String]())

    interpreter.toSecureRequest(e, Some(baseUrl))(bearerToken)((projectId, id)) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data)
        }
    }
  }
}
