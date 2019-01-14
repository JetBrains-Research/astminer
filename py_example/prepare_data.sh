#!/usr/bin/env bash

git clone https://github.com/apache/kafka.git data/project1
git clone https://github.com/apache/incubator-heron.git data/project2
cd ../
./gradlew processPyExample
cd py_example