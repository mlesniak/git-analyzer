package com.mlesniak.git.analyzer.source

import java.nio.file.Path
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.io.path.isDirectory

/**
 * Create a parser for git logs for a repository as specified by the
 * constructor parameter.
 */
class GitLogParser(private val repository: Path) {
    /**
     * Access repository, read all commits and return a list of all
     * commits ready to be processed.
     */
    fun readRepository(): List<Commit> {
        assert(repository.isDirectory())

        val log = getLogDataFromGit(repository)
        val rawCommits = groupByCommits(log)
        return rawCommits.map { parseCommit(it) }
    }

    private fun parseCommit(lines: List<String>): Commit {
        val sdf = SimpleDateFormat("E MMM d HH:mm:ss yyyy Z")

        val id = lines[0].split(" ")[1]
        val properties = mutableMapOf<String, String>()
        var lineIdx = 1
        while (lines[lineIdx].isNotEmpty()) {
            val curLine = lines[lineIdx]
            val parts = curLine.split(":", limit = 2)
            properties[parts[0]] = parts[1].trim()
            lineIdx++
        }
        val author = properties["Author"]!!
        val rawDate = properties["Date"]!!
        val date = sdf.parse(rawDate)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        val message = StringBuilder()
        for (i in lineIdx until lines.size) {
            if (lines[i].isEmpty()) {
                break
            }
            message.append(lines[i].trim())
        }

        val filenames = mutableListOf<Path>()
        val filenamePattern = Pattern.compile("diff --git a/(.*) b/.*")
        for (i in 3 until lines.size) {
            val line = lines[i]
            val matcher = filenamePattern.matcher(line)
            if (matcher.matches()) {
                val filename = matcher.group(1)
                val fullPath = Path.of(repository.toAbsolutePath().toString(), filename)
                filenames.add(fullPath)
            }
        }

        return Commit(
            id = id,
            author = author,
            date = date,
            filenames = filenames,
            message = message.toString(),
        )
    }

    private fun groupByCommits(log: List<String>): List<List<String>> {
        val commits = mutableListOf<List<String>>()
        val segmentBegin = Pattern.compile("^commit \\S{40}$")
        var commit: MutableList<String> = mutableListOf()
        log.forEach { line ->
            if (segmentBegin.matcher(line).find()) {
                // Add previous commit to list.
                if (commit.isNotEmpty()) {
                    commits.add(commit)
                }
                commit = mutableListOf()
            }
            commit.add(line)
        }

        // Add final commit as well.
        commits.add(commit)

        return commits
    }

    private fun getLogDataFromGit(path: Path): List<String> {
        val process = Runtime.getRuntime().exec(
            arrayOf("/usr/local/bin/git", "log", "-p"),
            arrayOf(),
            path.toFile()
        )
        return process
            .inputReader()
            .lines()
            .collect(Collectors.toList())
    }
}
