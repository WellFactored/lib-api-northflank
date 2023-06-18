package com.wellfactored.api.northflank
package endpoints

import io.circe
import io.circe.generic.semiauto.deriveCodec
import sttp.model.StatusCode
import sttp.tapir.EndpointOutput.OneOfVariant
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{emptyOutputAs, oneOfVariant}

sealed trait NorthflankError {
  def widen: NorthflankError = this
}

object NorthflankError {

  case class BadRequest(messages: List[String]) extends NorthflankError
  case object NotFound extends NorthflankError

  implicit val badRequestCodec: circe.Codec[BadRequest] = deriveCodec

  val badRequestVariant: OneOfVariant[BadRequest] = oneOfVariant(StatusCode.BadRequest, jsonBody[BadRequest])
  val notFoundVariant: OneOfVariant[NotFound.type ] = oneOfVariant(StatusCode.NotFound, emptyOutputAs(NotFound))

}
