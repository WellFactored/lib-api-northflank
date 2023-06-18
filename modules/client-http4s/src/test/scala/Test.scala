package com.wellfactored.api.northflank

import client.http4s.{PlanClient, ProjectClient, ServicesClient}

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._
import org.http4s.ember.client.EmberClientBuilder

import scala.concurrent.duration.DurationInt

object Test extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    EmberClientBuilder
      .default[IO]
      .withTimeout(55.seconds)
      .build
      .use { client =>
        val servicesClient = new ServicesClient[IO](args.head)(client)
        val plansClient    = new PlanClient[IO](client)
        val projectClient  = new ProjectClient[IO](args.head)(client)

        {
          for {
            plans    <- EitherT(plansClient.getPlans)
            projects <- EitherT(projectClient.getProjects)
            _ = println(projects)
            deployments <- EitherT(servicesClient.getServices).map(_.filter(_.serviceType == "deployment"))
            details     <- deployments.parTraverse(summary => EitherT(servicesClient.getService(summary.id)))
          } yield {
            val monthlyPrices: Map[String, BigDecimal] = plans.groupBy(_.id).view.mapValues(_.head.amountPerMonth).toMap

            details.foldLeft(BigDecimal(0.0)) {
              case (total, service) =>
                val planCost = monthlyPrices(service.billing.deploymentPlan)
                total + planCost * service.deployment.instances
            }
          }
        }.value.map(println)
      }
      .as(ExitCode.Success)
}
