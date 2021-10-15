#!/bin/bash

# cd into current directory
cd $(dirname ${BASH_SOURCE})
# move two directory levels upwards
cd ../..

./gradlew wiremock:clean wiremock:build

docker build -t wiremock wiremock/ && docker run -p 9000:9000 wiremock