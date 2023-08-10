package com.wellfactored.api.northflank
package client.http4s

import endpoints.{NorthflankError, PaginationInput, ServiceEndpoints}
import model.Service

import cats.effect.Async
import cats.syntax.all._
import org.http4s.Uri
import org.http4s.client.Client
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.{auth, oneOf}

class ServicesClient[F[_]: Async](bearerToken: String, projectName: String, baseUrl: Uri)(httpClient: Client[F]) {
  val interpreter: Http4sClientInterpreter[F] = Http4sClientInterpreter()

  def getServices: F[Either[String, List[Service.Summary]]] = {
    val e = ServiceEndpoints.getServices
      .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))
      .securityIn(auth.bearer[String]())

    interpreter.toSecureRequest(e, Some(baseUrl))(bearerToken)(PaginationInput(projectName)) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data.services)
        }
    }
  }

  def getService(id: String): F[Either[String, Service.Detail]] = {
    val e = ServiceEndpoints.getService
      .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))
      .securityIn(auth.bearer[String]())

    interpreter.toSecureRequest(e, Some(baseUrl))(bearerToken)((projectName, id)) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data)
        }
    }
  }
}
