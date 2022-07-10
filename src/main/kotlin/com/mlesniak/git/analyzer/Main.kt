package com.mlesniak.git.analyzer

import com.mlesniak.git.analyzer.analysis.DomainExperts
import com.mlesniak.git.analyzer.source.GitLogParser
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

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
    val gitRepositoryPath = validateCommandLine(args)

    val parser = GitLogParser(gitRepositoryPath)
    val commits = parser.readRepository()

    // TODO(mlesniak) return cool structure for json parsing
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

private fun validateCommandLine(args: Array<String>): Path {
    if (args.isEmpty()) {
        println("No path to git repository provided")
        exitProcess(1)
    }
    val gitRepositoryPath = Path.of(args[0])
    if (!gitRepositoryPath.isDirectory()) {
        println("Path is no a directory")
        exitProcess(1)
    }
    return gitRepositoryPath
}
