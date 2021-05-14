#!/bin/bash

IMAGE_NAME="voudy/astminer"
SHADOW_JAR_PATH="build/shadow/astminer.jar"

if [[ "$(docker images -q $IMAGE_NAME 2> /dev/null)" == "" ]]; then
  echo "Docker image not found, will use $SHADOW_JAR_PATH";
  if ! [[ -f "$SHADOW_JAR_PATH" ]]; then
    echo "$SHADOW_JAR_PATH not found, building" 
    ./gradlew shadowJar
  fi
  java -jar $SHADOW_JAR_PATH "$@"
else
  echo "Running astminer in docker"
  docker run --rm $IMAGE_NAME "$@"
fi

