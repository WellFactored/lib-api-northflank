package com.wellfactored.api.northflank
package client.http4s

import endpoints.{NorthflankError, ProjectEndpoints}
import model.Project

import cats.effect.Async
import cats.syntax.all._
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.{auth, oneOf}

class ProjectClient[F[_]: Async](bearerToken: String)(httpClient: Client[F]) {
  val interpreter: Http4sClientInterpreter[F] = Http4sClientInterpreter()

  def getProjects: F[Either[String, List[Project.Summary]]] = {
    val e =
      ProjectEndpoints.getProjects
        .errorOut(oneOf[NorthflankError](NorthflankError.badRequestVariant, NorthflankError.notFoundVariant))
        .securityIn(auth.bearer[String]())

    interpreter.toSecureRequest(e, Some(uri"https://api.northflank.com"))(bearerToken)(()) match {
      case (request, handler) =>
        httpClient.run(request).use(handler).map {
          handleResponse(request, _).map(_.data.projects)
        }
    }
  }
}
