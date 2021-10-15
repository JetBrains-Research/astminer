#!/bin/bash

IMAGE_NAME="voudy/astminer"
SHADOW_JAR_PATH="build/shadow/astminer.jar"

if ! [[ -f "$SHADOW_JAR_PATH" ]]; then
  echo "$SHADOW_JAR_PATH not found, building"
  ./gradlew shadowJar
fi

if [[ "$(docker images -q $IMAGE_NAME 2> /dev/null)" == "" ]]; then
  echo "Docker image not found, will use $SHADOW_JAR_PATH";
  java -jar $SHADOW_JAR_PATH "$1"
else
  echo "Running astminer in docker"

#  mount config file, log, input dir and output dir to docker
#  convert all paths to be absolute
  CONFIG_PATH=$1
  INPUT_FOLDER=$(grep inputDir "$1" | cut -c 11-)
  OUTPUT_FOLDER=$(grep outputDir "$1" | cut -c 12-)
  touch log.txt
  LOG_PATH="log.txt"
  docker run \
    -v "$(pwd)"/"$CONFIG_PATH":/astminer/"$CONFIG_PATH" \
    -v "$(pwd)"/"$OUTPUT_FOLDER":/astminer/"$OUTPUT_FOLDER" \
    -v "$(pwd)"/"$INPUT_FOLDER":/astminer/"$INPUT_FOLDER" \
    -v "$(pwd)"/"$SHADOW_JAR_PATH":/astminer/astminer.jar \
    -v "$(pwd)"/"$LOG_PATH":/astminer/log.txt \
    --rm $IMAGE_NAME "$1"
fi

