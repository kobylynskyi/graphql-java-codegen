#!/bin/bash

RELEASE_VERSION=$1
RELEASE_VERSION_ESCAPED=${RELEASE_VERSION//./\\.}

set_version_in_file() {
  # for mac add '' after -i: sed -i '' "s...
  sed -i '' "s/$2[A-Z0-9.\-]*/$2$RELEASE_VERSION_ESCAPED/gw /tmp/sed.done" "$1"
  echo "Updated version in $1"
  grep --color -e "" /tmp/sed.done
}

set_version_in_file "build.gradle" "def graphqlCodegenVersion = '"

set_version_in_file "plugins/maven/graphql-java-codegen-maven-plugin/pom.xml" "<version.graphql-java-codegen>"

set_version_in_file "plugins/gradle/graphql-java-codegen-gradle-plugin/build.gradle" "def graphqlCodegenGradlePluginVersion = '"

set_version_in_file "plugins/gradle/example-server/build.gradle" "io.github.kobylynskyi.graphql.codegen\" version \""

set_version_in_file "plugins/gradle/example-client/build.gradle" "implementation \"io.github.kobylynskyi:graphql-java-codegen:"
set_version_in_file "plugins/gradle/example-client/build.gradle" "io.github.kobylynskyi.graphql.codegen\" version \""

set_version_in_file "plugins/gradle/example-client-kotlin/build.gradle" "id \"io.github.kobylynskyi.graphql.codegen\" version \""
set_version_in_file "plugins/gradle/example-client-kotlin/build.gradle" "def graphqlCodegenClientKotlinVersion = '"
set_version_in_file "plugins/gradle/example-client-kotlin/build.gradle" "implementation \"io.github.kobylynskyi:graphql-java-codegen:"

set_version_in_file "plugins/sbt/graphql-java-codegen-sbt-plugin/version.sbt" "version in ThisBuild := \""

set_version_in_file "plugins/sbt/graphql-java-codegen-sbt-plugin/src/sbt-test/graphql-codegen-sbt-plugin/example-client/version.sbt" "version in ThisBuild := \""
set_version_in_file "plugins/sbt/graphql-java-codegen-sbt-plugin/src/sbt-test/graphql-codegen-sbt-plugin/example-client/project/plugins.sbt" "graphql-codegen-sbt-plugin\" % \""

set_version_in_file "plugins/sbt/graphql-java-codegen-sbt-plugin/src/sbt-test/graphql-codegen-sbt-plugin/example-client-scala/version.sbt" "version in ThisBuild := \""
set_version_in_file "plugins/sbt/graphql-java-codegen-sbt-plugin/src/sbt-test/graphql-codegen-sbt-plugin/example-client-scala/project/plugins.sbt" "graphql-codegen-sbt-plugin\" % \""

set_version_in_file "plugins/sbt/graphql-java-codegen-sbt-plugin/src/sbt-test/graphql-codegen-sbt-plugin/simple/version.sbt" "version in ThisBuild := \""
set_version_in_file "plugins/sbt/graphql-java-codegen-sbt-plugin/src/sbt-test/graphql-codegen-sbt-plugin/simple/project/plugins.sbt" "plugin.version\").orElse(Some(\""

# Exit clean
exit 0
