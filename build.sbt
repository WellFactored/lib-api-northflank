val circeVersion      = "0.14.10"
val cirisVersion      = "2.3.3"
val enumeratumVersion = "1.7.4"
val http4sVersion     = "0.23.28"
val sttpVersion       = "1.7.11"
val tapirVersion      = "1.11.2"

lazy val commonSettings = Seq(
  organization := "com.wellfactored",
  idePackagePrefix := Some("com.wellfactored.api.northflank"),
  githubTokenSource := TokenSource.Or(TokenSource.GitConfig("github.token"), TokenSource.Environment("GITHUB_TOKEN")),
  githubOwner := "wellfactored",
  githubRepository := "lib-api-northflank",
  scalaVersion := "3.4.2",
  startYear := Some(2021),
  scalacOptions := commonScalacOptions,
  versionScheme := Some("early-semver"),
  idePackagePrefix := Some("com.wellfactored.api.northflank")
)

lazy val root = (project in file("."))
  .settings(name := "lib-api-northflank")
  .dependsOn(model, endpoints, http4sClient)
  .aggregate(model, endpoints, http4sClient)
  .settings(commonSettings)
  .settings(publish / skip := true)

lazy val model =
  project
    .in(file("modules/model"))
    .settings(name := "api-northflank-model")
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.beachape"                %% "enumeratum" % enumeratumVersion,
        "com.softwaremill.sttp.model" %% "core"       % sttpVersion
      )
    )

lazy val endpoints =
  project
    .in(file("modules/endpoints"))
    .settings(name := "api-northflank-endpoints")
    .settings(commonSettings)
    .dependsOn(model)
    .aggregate(model)
    .settings(
      libraryDependencies ++= Seq(
        "io.circe"                    %% "circe-generic"    % circeVersion,
        "com.beachape"                %% "enumeratum-circe" % enumeratumVersion,
        "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
        "com.softwaremill.sttp.tapir" %% "tapir-cats"       % tapirVersion,
        "com.softwaremill.sttp.tapir" %% "tapir-enumeratum" % tapirVersion
      )
    )

lazy val http4sClient =
  project
    .in(file("modules/client-http4s"))
    .settings(name := "api-northflank-client-http4s")
    .settings(commonSettings)
    .dependsOn(endpoints)
    .aggregate(endpoints)
    .settings(
      libraryDependencies ++= Seq(
        "com.softwaremill.sttp.tapir" %% "tapir-http4s-client" % tapirVersion,
        "org.http4s"                  %% "http4s-ember-client" % http4sVersion
      )
    )

lazy val commonScalacOptions = Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps"
)
