package com.wellfactored.api.northflank
package endpoints

import model.Plan

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Endpoint
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody

object PlansEndpoints {

  case class GetPlansResponseData(plans: List[Plan])
  implicit val getPlansResponseDataCodec: Codec[GetPlansResponseData] = deriveCodec
  val getPlans: Endpoint[Unit, Unit, Unit, PaginatedResults[GetPlansResponseData], Any] =
    v1Endpoint.in("plans").out(jsonBody[PaginatedResults[GetPlansResponseData]]).get
}
