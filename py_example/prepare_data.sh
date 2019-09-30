#!/usr/bin/env bash

if [[ ! -d "data/project1" ]]; then
    git clone https://github.com/apache/kafka.git data/project1
fi
if [[ ! -d "data/project2" ]]; then
    git clone https://github.com/apache/incubator-heron.git data/project2
fi
cd ../
./gradlew processPyExample --no-daemon
cd py_example
