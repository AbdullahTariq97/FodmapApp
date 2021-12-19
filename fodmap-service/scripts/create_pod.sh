#!/bin/bash

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
module_dir="${script_dir}/.."
project_dir="${script_dir}/../../"
cd $module_dir

docker image rm -f local/fodmap-service
docker build -t local/fodmap-service .
kubectl --context=docker-desktop --namespace=default apply -f pod.yml

