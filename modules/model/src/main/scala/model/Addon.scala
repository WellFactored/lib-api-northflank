package com.wellfactored.api.northflank
package model

object Addon {
  case class Deployment(replicas: Long, storageClass: String, storageSize: Long, planId: String, region: String)

  case class SummarySpec(`type`: String)
  case class Summary(id:         String, appId: String, name: String, description: String, spec: SummarySpec)

  case class Config(versionTag: String, lifecycleStatus: String, deployment: Addon.Deployment)
  case class DetailSpec(`type`: String, config:          Config)
  case class Detail(id:         String, appId:           String, name: String, desctiption: String, spec: DetailSpec)

}
