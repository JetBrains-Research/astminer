#!/usr/bin/env bash

mkdir -p external
cd external
git clone https://github.com/egor-bogomolov/joern.git joern

cd joern
./gradlew deploy -x test
cd ../..
