package com.wellfactored.api.northflank
package endpoints

import model.Addon
import model.Addon.{Detail, DetailSpec}

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{PublicEndpoint, Schema, path, given}

object AddonEndpoints {

  implicit val addonSummarySpecCodec: Codec[Addon.SummarySpec] = deriveCodec
  implicit val addonSummaryCodec:     Codec[Addon.Summary]     = deriveCodec
  implicit val addonDeploymentCodec:  Codec[Addon.Deployment]  = deriveCodec
  implicit val addonConfigCodec:      Codec[Addon.Config]      = deriveCodec
  implicit val addonDetailSpecCodec:  Codec[Addon.DetailSpec]  = deriveCodec
  implicit val addonDetailCodec:      Codec[Addon.Detail]      = deriveCodec

  case class AddonResults(addons: List[Addon.Summary])
  implicit val getAddonsResultsCodec: Codec[AddonResults] = deriveCodec

  val getAddons: PublicEndpoint[PaginationInput[String], Unit, PaginatedResults[AddonResults], Any] =
    v1Endpoint
      .in("projects")
      .in(path[String]("projectId"))
      .in("addons")
      .paginatedOut[AddonResults]
      .get

  case class GetAddonResponseBody(data: Addon.Detail)
  given getAddonResponseBodyCodec: Codec[GetAddonResponseBody] = deriveCodec

  implicit val detailSpecSchema:           Schema[DetailSpec]           = Schema.derived[DetailSpec]
  implicit val detailSchema:               Schema[Detail]               = Schema.derived[Detail]
  implicit val getAddonResponseBodySchema: Schema[GetAddonResponseBody] = Schema.derived[GetAddonResponseBody]

  val getAddon: PublicEndpoint[(String, String), Unit, GetAddonResponseBody, Any] =
    v1Endpoint
      .in("projects")
      .in(path[String]("projectId"))
      .in("addons")
      .in(path[String]("addonId"))
      .out(jsonBody[GetAddonResponseBody])
      .get

}
