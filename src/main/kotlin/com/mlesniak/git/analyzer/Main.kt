package com.mlesniak.git.analyzer

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mlesniak.git.analyzer.analysis.PackageExperts
import com.mlesniak.git.analyzer.source.GitLogParser

fun main(args: Array<String>) {
    val config = Configuration.parse(args)

    val gitLogParser = GitLogParser(config.directory)
    val commits = gitLogParser.readRepository()
    val result = when (config.analysis) {
        Analysis.PackageExperts -> PackageExperts(commits, config.period).get()
    }

    printResultAsJson(result)
}

private fun printResultAsJson(result: Any) {
    val json = jacksonObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, true)
        .writeValueAsString(result)
    println(json)
}
