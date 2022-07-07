package com.mlesniak.main

import com.mlesniak.main.analyzer.DomainExperts

fun main(args: Array<String>) {
    val commits = getCommits(args[0])

    println("Number of commits: ${commits.size}")
    val de = DomainExperts(commits)
    de.analyze()

    val m = de.get()
    m.forEach { pckg ->
        println("\n${pckg.key}")

        val occurences = pckg.value
            .toList()
            .sortedBy { (_, occ) -> occ }
            .reversed()
            .toMap()

        occurences.forEach { author ->
            println("${author.key}\t${author.value}")
        }
    }

    // what now?
}
