package com.wellfactored.api.northflank

import model.{Plan, Project}

import cats.syntax.all._
import io.circe.Decoder.Result
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax.EncoderOps
import io.circe._
import sttp.model.Uri
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{DecodeResult, Endpoint, Mapping, PublicEndpoint, Schema, Validator, endpoint, query}

package object endpoints {
  val v1Endpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = endpoint.in("v1")

  case class Pagination(hasNextPage: Boolean, count: Long)
  object Pagination {
    implicit val codec: Codec[Pagination] = deriveCodec
  }

  case class PaginatedResults[T: Decoder](data: T, pagination: Option[Pagination])
  object PaginatedResults {
    implicit def codec[T: Codec]: Codec[PaginatedResults[T]] = deriveCodec
  }

  case class PaginationParams(perPage: Option[Int], page: Option[Int], cursor: Option[String])
  object PaginationParams {
    val empty: PaginationParams = PaginationParams(None, None, None)
  }
  case class PaginationInput[T](t: T, paginationParams: PaginationParams = PaginationParams.empty)

  def paginationMapping[T]: Mapping[(T, Option[Int], Option[Int], Option[String]), PaginationInput[T]] =
    new Mapping[(T, Option[Int], Option[Int], Option[String]), PaginationInput[T]] {
      override def rawDecode(l: (T, Option[Int], Option[Int], Option[String])): DecodeResult[PaginationInput[T]] = {
        val (t, pp, p, c) = l
        DecodeResult.Value(PaginationInput[T](t, PaginationParams(pp, p, c)))
      }

      override def encode(h: PaginationInput[T]): (T, Option[Int], Option[Int], Option[String]) =
        (h.t, h.paginationParams.perPage, h.paginationParams.page, h.paginationParams.cursor)

      override def validator: Validator[PaginationInput[T]] = Validator.pass
    }

  implicit class PaginationSyntax[I, E, R](e: PublicEndpoint[I, E, Unit, R]) {
    def paginatedOut[O: Codec: Schema]: PublicEndpoint[PaginationInput[I], E, PaginatedResults[O], R] =
      e.in(query[Option[Int]]("per_page"))
        .in(query[Option[Int]]("page"))
        .in(query[Option[String]]("cursor"))
        .mapIn(paginationMapping[I])
        .out(jsonBody[PaginatedResults[O]])
  }

  implicit val projectSummaryCodec: Codec[Project.Summary] = deriveCodec
  implicit val planCodec:           Codec[Plan]            = deriveCodec

  implicit val uriCodec: Codec[Uri] = new Codec[Uri] {
    override def apply(a: Uri): Json = a.toString().asJson
    override def apply(c: HCursor): Result[Uri] = c.as[String].flatMap { s =>
      Uri.parse(s).leftMap(err => DecodingFailure(err, c.history))
    }
  }

  implicit val uriSchema: Schema[Uri] = Schema.string

}
