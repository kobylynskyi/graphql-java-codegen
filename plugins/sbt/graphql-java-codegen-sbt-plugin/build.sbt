import sbtrelease.ReleaseStateTransformations._

name := "graphql-codegen-sbt-plugin"
// must be equals to oss Group Id
organization := "io.github.jxnu-liguobin"
val jValidationVersion = settingKey[String]("default java Validation api").withRank(KeyRanks.Invisible)
jValidationVersion := "2.0.1.Final"

// keep version is equals with parent project `graphql-java-codegen`.
// Plugin don't need to care about the scala version, just the SBT version.
lazy val `graphql-codegen-sbt-plugin` = Project(id = "graphql-codegen-sbt-plugin", base = file(".")).
  enablePlugins(SbtPlugin, BuildInfoPlugin).
  settings(Publishing.publishSettings).
  settings(
    sbtPlugin := true,
    scriptedBufferLog := false,
    scriptedLaunchOpts += s"-Dplugin.version=${version.value}",
    scalacOptions += "-target:jvm-1.8",
    releaseIgnoreUntrackedFiles := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      releaseStepCommandAndRemaining("^ scripted"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("^ publishSigned"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
    libraryDependencies ++= Seq(
      "io.github.kobylynskyi" % "graphql-java-codegen" % (version in ThisBuild).value
    ),
    buildInfoKeys := Seq[BuildInfoKey](name, version, sbtVersion, jValidationVersion),
    buildInfoPackage := "io.github.dreamylost.graphql.codegen"
  )