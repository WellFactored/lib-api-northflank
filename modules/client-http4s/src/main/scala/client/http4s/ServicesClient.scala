package com.wellfactored.api.northflank
package client.http4s

import endpoints.{NorthflankError, PaginationInput, ServiceEndpoints}
import model.Service

import cats.effect.Async
import cats.syntax.all._
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.{auth, oneOf}

class ServicesClient[F[_]: Async](bearerToken: String)(httpClient: Client[F]) {
  val interpreter: Http4sClientInterpreter[F] = Http4sClientInterpreter()

  def getServices: F[Either[String, List[Service.Summary]]] = {
    val e = ServiceEndpoints.getServices
      .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))
      .securityIn(auth.bearer[String]())

    interpreter.toSecureRequest(e, Some(uri"https://api.northflank.com"))(bearerToken)(PaginationInput("qm-springboard")) match {
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

    interpreter.toSecureRequest(e, Some(uri"https://api.northflank.com"))(bearerToken)(("qm-springboard", id)) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data)
        }
    }
  }
}
