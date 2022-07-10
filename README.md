    java -jar build/libs/git-analyzer-all.jar /Users/m/Documents/junit5 >junit.json
    export FILE=junit.json
    cat $FILE| jq .$(cat $FILE|jq 'keys[]'|fzf)