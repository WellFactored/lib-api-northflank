package com.wellfactored.api.northflank
package client.http4s

import endpoints.{NorthflankError, PlansEndpoints}
import model.Plan

import cats.effect.Async
import cats.syntax.all._
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.oneOf

class PlanClient[F[_]: Async](httpClient: Client[F]) {
  val interpreter: Http4sClientInterpreter[F] = Http4sClientInterpreter()

  def getPlans: F[Either[String, List[Plan]]] = {
    val e = PlansEndpoints.getPlans
      .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))

    interpreter.toRequest(e, Some(uri"https://api.northflank.com"))(()) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data.plans)
        }
    }
  }
}
