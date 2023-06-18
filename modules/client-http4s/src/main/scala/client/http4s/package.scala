package com.wellfactored.api.northflank
package client

import endpoints.NorthflankError

import cats.syntax.all._
import org.http4s.{Request, Uri => Http4sUri}
import sttp.model.{Uri => SttpUri}
import sttp.tapir.DecodeResult
import sttp.tapir.DecodeResult.Value

package object http4s {
  implicit class SttpUrlSyntax(val url: SttpUri) {
    def toHttp4s: Http4sUri = Http4sUri.unsafeFromString(url.toString)
  }

  implicit class Http4sUrlSyntax(val url: Http4sUri) {
    def toSttp: SttpUri = SttpUri.unsafeParse(url.toString)
  }

  def handleResponse[F[_], T](request: Request[F], result: DecodeResult[Either[NorthflankError, T]]): Either[String, T] =
    result match {
      case Value(Right(result))                  => result.asRight
      case Value(Left(NorthflankError.NotFound)) => s"Not found: ${request.uri}".asLeft
      case Value(Left(error))                    => error.toString.asLeft
      case failure: DecodeResult.Failure => failure.toString.asLeft
    }

}
