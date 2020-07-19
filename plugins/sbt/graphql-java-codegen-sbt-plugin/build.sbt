import Dependencies._

name := "graphql-codegen-sbt-plugin"
// must be equals to oss Group Id
organization := "io.github.jxnu-liguobin"

//because in ci, can not find maven local
resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

// publish only root project
//publish / skip := true

//keep version is equals with parent project `graphql-java-codegen`
lazy val `graphql-codegen-sbt-plugin` = Project(id = "graphql-codegen-sbt-plugin", base = file(".")).
  enablePlugins(SbtPlugin, Publishing).
  settings(
    sbtPlugin := true,
    scalaVersion := Versions.scala212,
    crossScalaVersions := List(Versions.scala212, Versions.scala211),
    scalacOptions += "-target:jvm-1.8").
  settings(Compiles.selfDependencies)