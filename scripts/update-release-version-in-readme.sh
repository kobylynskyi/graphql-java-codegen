#!/bin/bash

RELEASE_VERSION=$1
RELEASE_VERSION_ESCAPED=${RELEASE_VERSION//./\\.}

set_version_in_file() {
  sed -i '' "s/$2[A-Z0-9.\-]*/$2$RELEASE_VERSION_ESCAPED/gw /tmp/sed.done" "$1"
  echo "Updated version in $1"
  grep --color -e "" /tmp/sed.done
}

set_version_in_file "plugins/gradle/README.md" "id \"io.github.kobylynskyi.graphql.codegen\" version \""
set_version_in_file "plugins/gradle/README.md" "io.github.kobylynskyi.graphql.codegen:graphql-codegen-gradle-plugin:"
set_version_in_file "plugins/maven/README.md" "<version>"

# Exit clean
exit 0
