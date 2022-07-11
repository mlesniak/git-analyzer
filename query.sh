#!/bin/bash

if [ -z "$1" ]
then
  echo "Missing parameter: full path to git repository"
  exit 1
fi

path=$1
maxDate=$2

output=$(basename "$path").json

if [ -e "$output" ]
then
  echo "Using existing output file $output. Delete this file to let it be generated again."
else
  echo "Generating output file $output"
  java -jar build/libs/git-analyzer-all.jar /Users/m/Documents/junit5 >"$output"
fi
# shellcheck disable=SC2094
jq .$(jq 'keys[]' <"$output"|fzf) <"$output"