#!/bin/bash

IMAGE_NAME="voudy/astminer"
SHADOW_JAR_PATH="build/shadow/astminer.jar"

if [[ "$(docker images -q $IMAGE_NAME 2> /dev/null)" == "" ]]; then
  echo "Can't find docker image, will compile from sources"
  ./gradlew shadowJar
  java -jar $SHADOW_JAR_PATH "$@"
else
  echo "Run ASTMiner inside docker"
  docker run --rm voudy/astminer "$@"
fi

