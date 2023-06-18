package com.wellfactored.api.northflank
package model

import sttp.model.Uri

import java.time.Instant

sealed trait JobType

case class BillingSummary(deploymentPlan: String)

case class BuildStatus(status: String, lastTransitionTime: Instant)

object Docker {
  case class Command(enabled:    Boolean, value: String)
  case class Entrypoint(enabled: Boolean, value: String)

  case class Detail(cmd: Command, entrypoint: Entrypoint)
}

object Storage {
  case class Summary(ephemeralStorage:     EphemeralStorage)
  case class EphemeralStorage(storageSize: Long)
}

case class VcsData(
  projectUrl:      Uri,
  projectType:     String,
  selfHostedVcsId: String,
  projectBranch:   String,
  publicRepo:      Boolean,
  dockerWorkDir:   String,
  dockerFilePath:  String
)

case class JobSummary(id: String, appId: String, name: String, description: String, jobType: JobType)


case class Plan(
  id:             String,
  name:           String,
  currency:       String,
  amountPerMonth: BigDecimal,
  amountPerHour:  BigDecimal,
  cpuResource:    BigDecimal,
  ramResource:    BigDecimal
)
