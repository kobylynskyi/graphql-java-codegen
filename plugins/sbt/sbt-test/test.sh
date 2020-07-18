#!/bin/bash
# check if the file gets created
sbt clean compile graphqlCodegen --debug
generateCode=target/scala-2.13/src_managed_graphql/io/github/kobylynskyi/graphql/test/model/User.java
if [ -f "$generateCode" ]; then
  echo "test successfully"
else
  echo "test failed"
  exit 1
fi
