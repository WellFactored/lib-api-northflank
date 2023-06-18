package com.wellfactored.api.northflank
package endpoints

import model.Service.DeploymentStatus
import model.Storage.EphemeralStorage
import model._

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{PublicEndpoint, path}

object ServiceEndpoints {
  implicit val serviceSummaryCodec:   Codec[Service.Summary]        = deriveCodec
  implicit val buildStatusCodec:      Codec[BuildStatus]            = deriveCodec
  implicit val deploymentStatusCodec: Codec[DeploymentStatus]       = deriveCodec
  implicit val serviceStatusCodec:    Codec[Service.ServiceStatus]  = deriveCodec
  implicit val billingSummaryCodec:   Codec[BillingSummary]         = deriveCodec
  implicit val vcsDataCodec:          Codec[VcsData]                = deriveCodec
  implicit val internalDetailCodec:   Codec[Service.InternalDetail] = deriveCodec
  implicit val deploymentCodec:       Codec[Service.Deployment]     = deriveCodec
  implicit val dockerCommandCodec:    Codec[Docker.Command]         = deriveCodec
  implicit val dockerEntrypointCodec: Codec[Docker.Entrypoint]      = deriveCodec
  implicit val dockerDetailCodec:     Codec[Docker.Detail]          = deriveCodec
  implicit val ephemeralStorageCodec: Codec[EphemeralStorage]       = deriveCodec
  implicit val storageSummaryCodec:   Codec[Storage.Summary]        = deriveCodec
  implicit val serviceDetailCodec:    Codec[Service.Detail]         = deriveCodec

  case class ServiceResults(services: List[Service.Summary])
  implicit val getServicesDataCodec: Codec[ServiceResults] = deriveCodec

  val getServices: PublicEndpoint[PaginationInput[String], Unit, PaginatedResults[ServiceResults], Any] =
    v1Endpoint
      .in("projects")
      .in(path[String]("projectId"))
      .paginatedOut[ServiceResults]

  case class GetServiceResponseBody(data: Service.Detail)
  implicit val getServiceResponseBodyCodec: Codec[GetServiceResponseBody] = deriveCodec
  val getService: PublicEndpoint[(String, String), Unit, GetServiceResponseBody, Any] =
    v1Endpoint
      .in("projects")
      .in(path[String]("projectId"))
      .in("services")
      .in(path[String]("serviceId"))
      .out(jsonBody[GetServiceResponseBody])

}
