#!/bin/bash

cd $(dirname ${BASH_SOURCE})
cd ../..

./gradlew wiremock:clean wiremock:build

docker build -t wiremock wiremock/ && docker run -p 9000:9000 wiremock