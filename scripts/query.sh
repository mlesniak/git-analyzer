#!/bin/bash

function checkInstalled() {
  which "$1" >/dev/null
  if [ $? -eq 1 ]
  then
    echo "Program $1 not installed"
    echo "Please install, e.g. via Homebrew:"
    echo "brew install $2"
    exit 1
  fi
}

checkInstalled jq jq
checkInstalled fzf fzf

if [ -z "$1" ]
then
  cat <<-EOF | sed 's/^ *//'
  This is a small demo script to show how to further process the JSON output in other unix tools.

  You have to specify a git repository path (and an optional period to analyze, e.g. last 100 days via "100d").
  You'll then be presented with a list of packages which have been touched in the period, can filter interactively
  and show all commiters ("domain experts") for this package.

  Note that the json output of git-analyzer for a particular period is cached in a .json file which is stored
  in the current directory. To rebuild, e.g. because the underlying repository has been updated, simply remove it.
EOF
  echo
  echo "Missing parameter: path to git repository to analyze"
  exit 1
fi

path=$1
if [ -n "$2" ]
then
  period="-p $2"
fi

output=$(basename "$path")$2.json

if [ -e "$output" ]
then
  echo "Using existing output file $output. Delete this file to regenerate it"
else
  echo "Generating output file $output..."
  # shellcheck disable=SC2086
  java -jar build/libs/git-analyzer-all.jar -d "$path" $period >"$output"
fi
echo
# shellcheck disable=SC2094
# shellcheck disable=SC2046
jq .$(jq 'keys[]' <"$output"|fzf) <"$output"
