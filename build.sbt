val circeVersion      = "0.14.3"
val cirisVersion      = "2.3.3"
val enumeratumVersion = "1.7.0"
val sttpVersion       = "1.5.2"
val tapirVersion      = "1.1.0"

lazy val commonSettings = Seq(
  organization := "com.wellfactored",
  idePackagePrefix := Some("com.wellfactored.api.northflank"),
  scalaVersion := "2.13.9",
  startYear := Some(2021),
  scalacOptions := commonScalacOptions,
  versionScheme := Some("early-semver"),
  idePackagePrefix := Some("com.wellfactored.api.northflank"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

lazy val root = (project in file("."))
  .settings(name := "lib-api-northflank")
  .dependsOn(model, endpoints)
  .aggregate(model, endpoints)
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
        "io.circe"                    %% "circe-generic"        % circeVersion,
        "com.beachape"                %% "enumeratum-circe"     % enumeratumVersion,
        "com.softwaremill.sttp.tapir" %% "tapir-json-circe"     % tapirVersion,
        "com.softwaremill.sttp.tapir" %% "tapir-cats"           % tapirVersion,
        "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"     % tapirVersion
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
