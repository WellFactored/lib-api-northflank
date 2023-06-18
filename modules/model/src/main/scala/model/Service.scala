package com.wellfactored.api.northflank
package model

import sttp.model.Uri

import java.time.Instant

object Service {
  case class DeploymentStatus(status: String, reason: String, lastTransitionTime: Instant)

  case class Summary(id: String, appId: String, name: String, description: String, serviceType: String)

  case class InternalDetail(nfObjectId: String, nfObjectType: String, repository: Uri, branch: String, buildSHA: String, deployedSHA: String)
  case class Deployment(
    region:    String,
    instances: Long
  )

  case class ServiceStatus(build: BuildStatus, deployment: DeploymentStatus)

  case class Detail(
    id:          String,
    appId:       String,
    name:        String,
    description: String,
    projectId:   String,
    serviceType: String,
    billing:     BillingSummary,
    deployment:  Service.Deployment
  )
}
