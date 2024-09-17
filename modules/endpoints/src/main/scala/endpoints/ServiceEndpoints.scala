package com.wellfactored.api.northflank
package endpoints

import model.*
import model.Service.DeploymentStatus

import cats.syntax.all.*
import io.circe
import io.circe.generic.semiauto.deriveCodec
import io.circe.{Decoder, Encoder}
import sttp.tapir
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{CodecFormat, Endpoint, PublicEndpoint, Schema, path, given}

object ServiceEndpoints {

  given circe.Codec[ProjectId]                                = stringCodec.iemap(ProjectId(_).asRight)(_.stringValue)
  given tapir.Codec[String, ProjectId, CodecFormat.TextPlain] = tapir.Codec.string.map(ProjectId(_))(_.stringValue)
  given Schema[ProjectId]                                     = Schema.string

  given io.circe.Codec[ServiceId]                             = stringCodec.iemap(ServiceId(_).asRight)(_.stringValue)
  given tapir.Codec[String, ServiceId, CodecFormat.TextPlain] = tapir.Codec.string.map(ServiceId.apply)(_.stringValue)
  given Schema[ServiceId]                                     = Schema.string

  given circe.Codec[Service.Summary]          = deriveCodec
  given circe.Codec[BuildStatus]              = deriveCodec
  given circe.Codec[DeploymentStatus]         = deriveCodec
  given circe.Codec[Service.ServiceStatus]    = deriveCodec
  given circe.Codec[BillingSummary]           = deriveCodec
  given circe.Codec[VcsData]                  = deriveCodec
  given circe.Codec[Service.InternalDetail]   = deriveCodec
  given circe.Codec[Service.Deployment]       = deriveCodec
  given circe.Codec[Docker.Command]           = deriveCodec
  given circe.Codec[Docker.Entrypoint]        = deriveCodec
  given circe.Codec[Docker.Detail]            = deriveCodec
  given circe.Codec[Storage.EphemeralStorage] = deriveCodec
  given storageSummaryCodec: circe.Codec[Storage.Summary] = deriveCodec
  given serviceDetailCodec:  circe.Codec[Service.Detail]  = deriveCodec

  case class ServiceResults(services: List[Service.Summary])
  given circe.Codec[ServiceResults] = deriveCodec

  val getServices: PublicEndpoint[PaginationInput[ProjectId], Unit, PaginatedResults[ServiceResults], Any] =
    v1Endpoint
      .in("projects")
      .in(path[ProjectId]("projectId"))
      .in("services")
      .paginatedOut[ServiceResults]

  case class GetServiceResponseBody(data: Service.Detail)
  given circe.Codec[GetServiceResponseBody] = deriveCodec
  val getService: Endpoint[Unit, (ProjectId, ServiceId), Unit, GetServiceResponseBody, Any] =
    v1Endpoint
      .in("projects")
      .in(path[ProjectId]("projectId"))
      .in("services")
      .in(path[ServiceId]("serviceId"))
      .out(jsonBody[GetServiceResponseBody])

}
