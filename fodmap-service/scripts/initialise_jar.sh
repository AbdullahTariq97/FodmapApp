#!/bin/bash

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
module_dir="${script_dir}/.."
project_dir="${script_dir}/../../"
cd $project_dir

./gradlew fodmap-service:clean
./gradlew  fodmap-service:build -x test
cd $module_dir/build/libs
chmod -x *.jar