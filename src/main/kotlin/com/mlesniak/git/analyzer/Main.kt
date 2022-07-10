package com.mlesniak.git.analyzer

import com.mlesniak.git.analyzer.analysis.DomainExperts
import com.mlesniak.git.analyzer.source.GitLogParser

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
    // TODO(mlesniak) Don't like this design: time consuming operation in constructor.
    val parser = GitLogParser(args[0])
    parser.readRepository()
    val commits = parser.commits()

    val domainExperts = DomainExperts(commits)
    domainExperts.analyze()

    val m = domainExperts.get()
    m.forEach { pckg ->
        println("\n${pckg.key}")
        for (author in pckg.value) {
            println("  ${author.key}: ${author.value}")
        }
    }
}
