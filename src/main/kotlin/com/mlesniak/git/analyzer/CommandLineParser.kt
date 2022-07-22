package com.mlesniak.git.analyzer

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

data class Configuration(
    val directory: Path,
    val period: Period,
    val analysis: Analysis,
) {
    companion object {
        fun parse(args: Array<String>): Configuration {
            val parser = ArgParser("git-analyzer")
            // TODO(mlesniak) Learn how this delegation is implemented internally.
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
            ).default(Analysis.PackageExperts)
            parser.parse(args)

            val internalPeriod = Period.parse("P$period")
            val internalDirectory = Path.of(directory)
            if (!internalDirectory.isDirectory()) {
                println("Path is no a directory")
                exitProcess(1)
            }

            return Configuration(
                internalDirectory,
                internalPeriod,
                analysis,
            )
        }
    }
}
