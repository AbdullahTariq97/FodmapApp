#!/bin/bash
# Running this shell script creates a fat jar contaning the wiremock dependency
cd $(dirname ${BASH_SOURCE})/../..
./gradlew wiremock:clean \
           wiremock:build \