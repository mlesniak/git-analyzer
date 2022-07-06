package com.mlesniak.main.analyzer

import com.mlesniak.main.Commit
import com.mlesniak.main.Commits
import java.io.File

/**
 * Map from package name to author, sorted by number of commits.
 */
// TODO(mlesniak) Date configuration as second parameter
class DomainExperts(val commits: Commits) {
    // package -> list of experts
    private val experts: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    fun analyze() {
        commits.forEach {
            process(it)
        }

        for (file in experts.keys) {
            // Ignore deleted and moved files for now.
            if (!File(file).exists()) {
                continue
            }

            val authors = experts[file]!!
            println("--- $file")
            authors.forEach { println(it) }
        }
    }

    private fun process(commit: Commit) {
        for (file in commit.filenames) {
            val m = experts.getOrDefault(file, mutableMapOf())
            val count = m[commit.author] ?: 0
            m[commit.author] = count + 1
            experts[file] = m
        }
    }
}