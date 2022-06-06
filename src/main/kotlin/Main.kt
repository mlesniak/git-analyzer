package com.mlesniak.main

fun main(args: Array<String>) {
    val commits = getCommits(args[0])

    commits.forEach { commit ->
        println(commit)
    }
}
