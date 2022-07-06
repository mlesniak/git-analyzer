package com.mlesniak.main

import com.mlesniak.main.analyzer.DomainExperts

fun main(args: Array<String>) {
    val commits = getCommits(args[0])

    println("Number of commits: ${commits.size}")
    val de = DomainExperts(commits)
    de.analyze()
    if (false) {
        println("No, didn't work")
    }

    // commits.forEach { commit ->
    //     println(commit)
    // }
}

fun bar() {

}