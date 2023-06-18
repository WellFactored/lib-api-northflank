package com.wellfactored.api.northflank
package endpoints

import model.Project

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Endpoint
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody

object ProjectEndpoints {
  case class ProjectResults(projects: List[Project.Summary])
  implicit val projectResultsDecoder: Codec[ProjectResults] = deriveCodec

  val getProjects: Endpoint[Unit, Unit, Unit, PaginatedResults[ProjectResults], Any] =
    v1Endpoint.in("projects").out(jsonBody[PaginatedResults[ProjectResults]]).get
}
