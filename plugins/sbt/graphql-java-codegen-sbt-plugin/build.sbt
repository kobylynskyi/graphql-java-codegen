import sbt.url

val javaValidationVersion = settingKey[String]("default Java Validation API")
  .withRank(KeyRanks.Invisible)
javaValidationVersion := "2.0.1.Final"

// keep version is equals with parent project `graphql-java-codegen`.
// Plugin don't need to care about the scala version, just the SBT version.
lazy val `graphql-codegen-sbt-plugin` = Project(id = "graphql-codegen-sbt-plugin", base = file("."))
  .enablePlugins(SbtPlugin, BuildInfoPlugin)
  .settings(
    sbtPlugin         := true,
    scriptedBufferLog := false,
    commands ++= Commands.value,
    scriptedLaunchOpts += s"-Dplugin.version=${version.value}",
    scalacOptions += "-target:jvm-1.8",
    libraryDependencies ++= Seq(
      "io.github.kobylynskyi"      % "graphql-java-codegen" % (ThisBuild / version).value,
      "org.freemarker"             % "freemarker"           % "2.3.31",
      "com.graphql-java"           % "graphql-java"         % "20.2",
      "com.fasterxml.jackson.core" % "jackson-databind"     % "2.12.1",
      "com.typesafe"               % "config"               % "1.4.2"
    ),
    buildInfoKeys    := Seq[BuildInfoKey](name, version, sbtVersion, javaValidationVersion),
    buildInfoPackage := "io.github.dreamylost.graphql.codegen"
  )

inThisBuild(
  List(
    name                   := "graphql-codegen-sbt-plugin",
    organization           := "io.github.jxnu-liguobin",
    sonatypeCredentialHost := "oss.sonatype.org",
    sonatypeRepository     := "https://oss.sonatype.org/service/local",
    homepage               := Some(url("https://github.com/kobylynskyi/graphql-java-codegen")),
    licenses               := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    developers := List(
      Developer(
        id = "jxnu-liguobin",
        name = "梦境迷离",
        email = "dreamylost@outlook.com",
        url = url("https://github/jxnu-liguobin")
      )
    )
  )
)
