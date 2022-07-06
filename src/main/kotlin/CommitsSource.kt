package com.mlesniak.main

import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.regex.Pattern
import java.util.stream.Collectors

typealias Commits = List<Commit>

// TODO(mlesniak) Add caching?
// TODO(mlesniak) Interface necessary?
data class Commit(
    val id: String,
    val author: String,
    val date: Instant,
    val filenames: List<String>,
    val message: String,
) {
    override fun toString(): String {
        val sb = java.lang.StringBuilder()

        sb.append(
            """
            $id
            $author
            $date
            
            $message
            
        """.trimIndent()
        )
        for (filename in filenames) {
            sb.append("- $filename\n")
        }

        return sb.toString()
    }
}

fun getCommits(repository: String): Commits {
    val log = getLog(repository)
    val rawCommits = groupByCommits(log)
    val commits = rawCommits.map { parse(it) }
    return commits
}

fun parse(lines: List<String>): Commit {
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
    val date = sdf.parse(rawDate).toInstant()

    val message = StringBuilder()
    for (i in lineIdx until lines.size) {
        if (lines[i].isEmpty()) {
            break
        }
        message.append(lines[i].trim())
    }

    val filenames = mutableListOf<String>()
    val filenamePattern = Pattern.compile("diff --git a/(.*) b/.*")
    for (i in 3 until lines.size) {
        val line = lines[i]
        val matcher = filenamePattern.matcher(line)
        if (matcher.matches()) {
            val filename = matcher.group(1)
            filenames.add(filename)
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

// TODO(mlesniak) Some fancy fold-logic here?
private fun groupByCommits(log: List<String>): MutableList<List<String>> {
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

    commits.add(commit)

    return commits
}

private fun getLog(path: String): List<String> {
    val process = Runtime.getRuntime().exec(
        arrayOf("/usr/local/bin/git", "log", "-p"),
        arrayOf(),
        File(path)
    )
    return process
        .inputReader()
        .lines()
        .collect(Collectors.toList())
}
