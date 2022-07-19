package com.mlesniak.git.analyzer

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mlesniak.git.analyzer.analysis.PackageExperts
import com.mlesniak.git.analyzer.source.GitLogParser
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import java.nio.file.Path
import java.time.Period
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgParser("git-analyzer")
    // TODO(mlesniak) Learn how this delegation is implemented internally.
    // TODO(mlesniak) Choose analysis
    val directory by parser.option(
        ArgType.String,
        shortName = "d",
        description = "Source code directory",
    ).required()
    val period by parser.option(
        ArgType.String,
        shortName = "p",
        description = "Period, e.g. 1y, 2w, 2w3d, ...",
    ).default("128y")
    parser.parse(args)

    val internalPeriod = Period.parse("P$period")
    val internalDirectory = Path.of(directory)

    if (!internalDirectory.isDirectory()) {
        println("Path is no a directory")
        exitProcess(1)
    }
    val gitLogParser = GitLogParser(internalDirectory)
    val commits = gitLogParser.readRepository()

    val experts = PackageExperts(commits, internalPeriod).get()

    printResult(experts)
}

private fun printResult(experts: Any) {
    val mapper = jacksonObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, true)
    val json = mapper.writeValueAsString(experts)
    println(json)
}
