package com.mlesniak.git.analyzer

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mlesniak.git.analyzer.analysis.PackageExperts
import com.mlesniak.git.analyzer.source.GitLogParser
import java.nio.file.Path
import java.time.Period
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val gitRepositoryPath = validateCommandLine(args)

    var p: Period =
        if (args.size == 2) {
            println("Parsing period ${args[1]}")
            Period.parse("P${args[1]}")
        } else {
            // 128 years in the past should be sufficient.
            Period.ofYears(128)
        }

    val parser = GitLogParser(gitRepositoryPath)
    val commits = parser.readRepository()

    // TODO(mlesniak) Real command line parser
    val experts = PackageExperts(commits, p).get()

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
