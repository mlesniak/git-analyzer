package com.mlesniak.git.analyzer.analysis

import com.mlesniak.git.analyzer.source.Commit
import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.Period
import java.util.SortedMap
import java.util.regex.Pattern
import kotlin.io.path.exists
import kotlin.io.path.extension

// Some helpful alias which we can't inline in the class.
typealias Package = String
typealias Author = String
typealias Occurences = Int

/**
 * Determine technical and domain experts for each package.
 *
 * To be useful for domain knowledge, we make the assumption that packages correspond
 * roughly to related business domains.
 */
class PackageExperts(
    commits: List<Commit>,
    period: Period = Period.ofYears(Int.MAX_VALUE)
) {
    private var packages: MutableMap<Package, MutableMap<Author, Occurences>> = mutableMapOf()

    init {
        val minDate = LocalDateTime.now().minus(period)

        commits
            .filter { it.date.isAfter(minDate) }
            .forEach { process(it) }
    }

    /**
     * Return a map of sorted packages (alphabetically) and for each package,
     * a map of authors and their commits in this package (sorted by number
     * of commits by author, ascending).
     */
    fun get(): SortedMap<Package, SortedMap<Author, Occurences>> {
        // Sort it once we're finished with everything. This is mainly to keep the
        // code cleaner and shouldn't have a big performance impact.
        val sortedOccurences = packages.map { entry ->
            val m = entry.value
            val sortedOccurences = m.toSortedMap { a1, a2 -> m[a1]!!.compareTo(m[a2]!!) }
            entry.key to sortedOccurences
        }

        val array = sortedOccurences.toTypedArray()
        return sortedMapOf(*array)
    }

    private fun process(commit: Commit) {
        for (file in commit.filenames) {
            if (!file.exists()) {
                continue
            }

            // We're only interested in files with package declaration.
            val pckg = determinePackage(file) ?: continue

            val m = packages.getOrDefault(pckg, mutableMapOf())
            val count = m[commit.author] ?: 0
            m[commit.author] = count + 1
            packages[pckg] = m
        }
    }

    private fun determinePackage(file: Path): String? {
        if (!(file.extension.endsWith("java") || file.extension.endsWith("kt"))) {
            return null
        }

        val pattern = Pattern.compile("\\s*package\\s+((\\w|\\.)+);?\$")
        file.toFile().readLines().forEach { line ->
            val matcher = pattern.matcher(line)
            if (matcher.matches()) {
                return matcher.group(1)
            }
        }

        return null
    }

    private fun fileDoesNotExist(file: String) = !File(file).exists()
}
