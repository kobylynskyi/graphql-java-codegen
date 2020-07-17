name := "graphql-java-codegen-sbt-plugin"

organization := "io.github.dreamylost"

lazy val scala212 = "2.12.12"
lazy val scala211 = "2.11.12"

scalaVersion := scala212

organization := "io.github.dreamylost"

enablePlugins(SbtPlugin)

sbtPlugin := true

crossScalaVersions := List(scala212, scala211)
scalacOptions += "-target:jvm-1.8"

libraryDependencies ++= Seq(
  "io.github.kobylynskyi" % "graphql-java-codegen" % "2.2.1"
)

lazy val `graphql-java-codegen-sbt-plugin` = Project(id = "graphql-java-codegen-sbt-plugin", base = file(".")).
  settings(publishSettings)

//publish by sbt publishSigned
lazy val publishSettings = Seq(
  credentials += Credentials(Path.userHome / ".ivy2" / ".sonatype_credentials"),
  usePgpKeyHex("1D664559C485101D646C585A9ECAAA3675D99811"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  sonatypeProfileName := organization.value,
  isSnapshot := version.value endsWith "SNAPSHOT",
  homepage := Some(url("https://github.com/jxnu-liguobin")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/jxnu-liguobin/graphql-java-codegen"),
      "scm:git@github.com:jxnu-liguobin/graphql-java-codegen.git"
    ))
)