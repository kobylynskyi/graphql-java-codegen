import Dependencies._

name := "graphql-codegen-sbt-plugin"
// must be equals to oss Group Id
organization := "io.github.jxnu-liguobin"

// publish only root project
//publish / skip := true

//keep version is equals with parent project `graphql-java-codegen`
lazy val `graphql-codegen-sbt-plugin` = Project(id = "graphql-codegen-sbt-plugin", base = file(".")).
  enablePlugins(SbtPlugin).
  settings(Publishing.publishSettings).
  settings(
    sbtPlugin := true,
    scalaVersion := Versions.scala212,
    crossScalaVersions := List(Versions.scala212, Versions.scala211),
    scalacOptions += "-target:jvm-1.8").
  settings(Compiles.selfDependencies)