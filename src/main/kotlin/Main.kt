package com.mlesniak.main

import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.regex.Pattern
import java.util.stream.Collectors

data class Commit(
    val id: String,
    val author: String,
    val Date: Instant,
    val filenames: List<String>,
    val message: String,
)

fun main(args: Array<String>) {
    val log = getLog(args[0])
    val rawCommits = groupByCommits(log)
    val commits = rawCommits.map { parse(it) }

    commits.forEach { commit ->
        println(commit)
    }
}

fun parse(lines: List<String>): Commit {
    val sdf = SimpleDateFormat("E MMM d HH:mm:ss yyyy Z")

    val id = lines[0].split(" ")[1]
    val author = lines[1].split(": ")[1]
    val rawDate = lines[2].split(':', ignoreCase = false, limit = 2)[1].trim()
    val date = sdf.parse(rawDate).toInstant()

    val message = StringBuilder()
    for (i in 4 until lines.size) {
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
        Date = date,
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
            if (commit.isNotEmpty()) {
                commits.add(commit)
            }
            commit = mutableListOf()
        }
        commit.add(line)
    }
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
