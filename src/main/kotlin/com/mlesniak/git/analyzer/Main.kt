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

enum class Analysis {
    PackageExperts
}

fun main(args: Array<String>) {
    val parser = ArgParser("git-analyzer")
    // TODO(mlesniak) Learn how this delegation is implemented internally.
    // TODO(mlesniak) Own data class for configuration
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
    val analysis by parser.option(
        ArgType.Choice<Analysis>(),
        shortName = "a",
        description = "Analysis to execute"
    ).required()
    parser.parse(args)

    val internalPeriod = Period.parse("P$period")
    val internalDirectory = Path.of(directory)

    if (!internalDirectory.isDirectory()) {
        println("Path is no a directory")
        exitProcess(1)
    }
    val gitLogParser = GitLogParser(internalDirectory)
    val commits = gitLogParser.readRepository()

    val result = when (analysis) {
        Analysis.PackageExperts -> PackageExperts(commits, internalPeriod).get()
        // This should never happen, since the argument
        // parser prevents using illegal values.
        else -> throw java.lang.IllegalStateException("Illegal analysis")
    }

    printResult(result)
}

private fun printResult(result: Any) {
    val mapper = jacksonObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, true)
    val json = mapper.writeValueAsString(result)
    println(json)
}
