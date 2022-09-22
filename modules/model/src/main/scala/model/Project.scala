package com.wellfactored.api.northflank
package model

import java.time.Instant

object Project {
  case class Deployment(region: String)

  case class Summary(
    id:          String,
    name:        String,
    description: String
  )

  case class Detail(
    id:          String,
    name:        String,
    description: String,
    deployment:  Deployment,
    createdAt:   Instant,
    services:    List[Service.Summary],
    jobs:        List[JobSummary],
    addons:      List[AddonSummary]
  )
}
