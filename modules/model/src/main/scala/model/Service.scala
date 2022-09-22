package com.wellfactored.api.northflank
package model

import sttp.model.Uri

import java.time.Instant

object Service {
  sealed trait ServiceType

  case class DeploymentStatus(status: String, reason: String, lastTransitionTime: Instant)

  case class Summary(id: String, appId: String, name: String, description: String, serviceType: ServiceType)

  case class InternalDetail(nfObjectId: String, nfObjectType: String, repository: Uri, branch: String, buildSHA: String, deployedSHA: String)
  case class Deployment(
    region:    String,
    instances: Long,
    internal:  InternalDetail
  )

  case class ServiceStatus(build: BuildStatus, deployment: DeploymentStatus)

  case class Detail(
    id:          String,
    appId:       String,
    name:        String,
    description: String,
    projectId:   String,
    serviceType: ServiceType,
    createdAt:   Instant,
    disabledCI:  Boolean,
    disabledCD:  Boolean,
    billing:     BillingSummary,
    status:      ServiceStatus,
    vcsData:     VcsData,
    deployment:  Service.Deployment,
    docker:      Docker.Detail,
    storage:     Storage.Summary
  )
}
