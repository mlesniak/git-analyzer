package com.mlesniak.git.analyzer

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mlesniak.git.analyzer.analysis.PackageExperts
import com.mlesniak.git.analyzer.source.GitLogParser
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val gitRepositoryPath = validateCommandLine(args)

    val parser = GitLogParser(gitRepositoryPath)
    val commits = parser.readRepository()

    // When we have multiple analysis modules, use a proper
    // command line parser.
    val experts = PackageExperts(commits).get()

    printResult(experts)
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

private fun printResult(experts: Any) {
    val mapper = jacksonObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, true)
    val json = mapper.writeValueAsString(experts)
    println(json)
}
