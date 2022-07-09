package com.mlesniak.main

import com.mlesniak.main.analyzer.DomainExperts

// For later json export
data class Package(
    val name: String,
    val authors: List<Author>
) {

}

data class Author(
    val name: String,
    val occurences: Int
)

fun main(args: Array<String>) {
    val commits = getCommits(args[0])

    println("Number of commits: ${commits.size}")
    val de = DomainExperts(commits)
    de.analyze()

    val m = de.get()
    m.forEach { pckg ->
        println("\n${pckg.key}")
        for (author in pckg.value) {
            println("  ${author.key}: ${author.value}")
        }
    }
}
