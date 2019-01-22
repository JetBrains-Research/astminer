#!/usr/bin/env bash

if [[ ! -d "java/kafka" ]]; then
    git clone https://github.com/apache/kafka.git java/kafka
fi
if [[ ! -d "java/incubator-heron" ]]; then
    git clone https://github.com/apache/incubator-heron.git java/incubator-heron
fi

if [[ ! -d "py/system-design-primer" ]]; then
    git clone https://github.com/donnemartin/system-design-primer.git py/system-design-primer
fi
if [[ ! -d "py/models" ]]; then
    git clone https://github.com/tensorflow/models.git py/models
fi
if [[ ! -d "py/public-apis" ]]; then
    git clone https://github.com/toddmotto/public-apis.git py/public-apis
fi