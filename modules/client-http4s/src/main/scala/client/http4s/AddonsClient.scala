package com.wellfactored.api.northflank
package client.http4s

import endpoints.{AddonEndpoints, NorthflankError, PaginationInput}
import model.Addon

import cats.effect.Async
import cats.syntax.all._
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.{auth, oneOf}

import scala.annotation.unused

class AddonsClient[F[_]: Async](bearerToken: String)(httpClient: Client[F]) {
  val interpreter: Http4sClientInterpreter[F] = Http4sClientInterpreter()

  @unused
  def getAddons: F[Either[String, List[Addon.Summary]]] = {
    val e = AddonEndpoints.getAddons
      .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))
      .securityIn(auth.bearer[String]())

    interpreter.toSecureRequest(e, Some(uri"https://api.northflank.com"))(bearerToken)(PaginationInput("qm-springboard")) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data.addons)
        }
    }
  }

  @unused
  def getAddon(id: String): F[Either[String, Addon.Detail]] = {
    val e = AddonEndpoints.getAddon
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
