name := "graphql-java-codegen-sbt-plugin"
organization := "io.github.dreamylost"

version := "0.1.1-SNAPSHOT"

scalaVersion := "2.12.12"

lazy val scala212 = "2.12.12"
lazy val scala211 = "2.11.12"

organization := "io.github.dreamylost"

enablePlugins(SbtPlugin)

sbtPlugin := true

libraryDependencies ++= Seq(
  "io.github.kobylynskyi" % "graphql-java-codegen" % "2.2.1"
)
crossScalaVersions := List(scala212, scala211)
scalacOptions += "-target:jvm-1.8"