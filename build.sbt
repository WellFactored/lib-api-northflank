val circeVersion      = "0.14.5"
val cirisVersion      = "2.3.3"
val enumeratumVersion = "1.7.3"
val http4sVersion     = "0.23.11"
val sttpVersion       = "1.7.2"
val tapirVersion      = "1.7.0"

lazy val commonSettings = Seq(
  organization := "com.wellfactored",
  idePackagePrefix := Some("com.wellfactored.api.northflank"),
  githubTokenSource := TokenSource.Or(TokenSource.GitConfig("github.token"), TokenSource.Environment("GITHUB_TOKEN")),
  githubOwner := "wellfactored",
  githubRepository := "lib-api-northflank",
  scalaVersion := "2.13.11",
  startYear := Some(2021),
  scalacOptions := commonScalacOptions,
  versionScheme := Some("early-semver"),
  idePackagePrefix := Some("com.wellfactored.api.northflank"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
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
  List(
    "-Wconf",
    List(
      "cat=deprecation:warning", // replacement for -deprecation
      "cat=unchecked:error", // replacement for -unchecked
      "cat=feature:warning", // replacement for -feature
      "cat=lint-byname-implicit:warning-summary", // bridges causes a bunch of these
      "cat=unused-imports:warning-verbose,", // warn on unused imports (so scalafix can remove them)
      "any:error" // anything else is an error
    ).mkString(",")
  ).mkString(":"),
  "-language:higherKinds",
  "-language:postfixOps"
)
