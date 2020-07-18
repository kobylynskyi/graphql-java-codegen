name := "graphql-codegen-sbt-plugin"

// must be equals to oss Group Id
organization := "io.github.jxnu-liguobin"

lazy val scala212 = "2.12.12"
lazy val scala211 = "2.11.12"

scalaVersion := scala212

enablePlugins(SbtPlugin)

sbtPlugin := true

// publish only root project
//publish / skip := true

crossScalaVersions := List(scala212, scala211)
scalacOptions += "-target:jvm-1.8"

libraryDependencies ++= Seq(
  "io.github.kobylynskyi" % "graphql-java-codegen" % "2.2.1"
)

lazy val `graphql-codegen-sbt-plugin` = Project(id = "graphql-codegen-sbt-plugin", base = file(".")).
  settings(publishSettings)

//publish by sbt publishSigned
lazy val publishSettings = Seq(
  credentials += Credentials(Path.userHome / ".ivy2" / ".sonatype_credentials"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  developers := List(
    Developer(
      id    = "dreamylost",
      name  = "梦境迷离",
      email = "dreamylost@outlook.com",
      url   = url("https://dreamylost.cn")
    )),
  sonatypeProfileName := organization.value,
  isSnapshot := version.value endsWith "SNAPSHOT",
  homepage := Some(url("https://github.com/jxnu-liguobin")),
  scmInfo := Some(
    ScmInfo(
      //it is fork from https://github.com/kobylynskyi/graphql-java-codegen
      url("https://github.com/jxnu-liguobin/graphql-java-codegen"),
      "scm:git@github.com:jxnu-liguobin/graphql-java-codegen.git"
    ))
)