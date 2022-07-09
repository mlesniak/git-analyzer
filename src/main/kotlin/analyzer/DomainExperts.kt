package com.mlesniak.main.analyzer

import com.mlesniak.main.Commit
import com.mlesniak.main.Commits
import java.io.File
import java.util.SortedMap
import java.util.regex.Pattern

typealias Package = String
typealias Author = String
typealias Occurences = Int

/**
 * Map from package name to author, sorted by number of commits.
 */
// TODO(mlesniak) Date configuration as second parameter to allow filtering
//                for a subset of dates.
class DomainExperts(private val commits: Commits) {
    // TODO(mlesniak) Domain specific class
    private var packages: MutableMap<Package, MutableMap<Author, Occurences>> = mutableMapOf()

    fun analyze() {
        commits.forEach { process(it) }

        packages = extendPackages(packages)
    }

    private fun extendPackages(packages: MutableMap<String, MutableMap<String, Int>>): MutableMap<String, MutableMap<String, Int>> {
        val ps: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

        for (pckg in packages) {
            extendPackage(ps, pckg)
        }

        return ps
    }

    private fun extendPackage(
        ps: MutableMap<String, MutableMap<String, Int>>,
        pckg: MutableMap.MutableEntry<String, MutableMap<String, Int>>
    ) {
        // println("\nPackage is ${pckg.key}")
        val parts = pckg.key.split(".")
        // org.junit.platform.suite.engine.testsuites
        for (i in 1..parts.size) {
            val parent = parts.take(i).joinToString(separator = ".")
            // println("\nCurrent package is $parent")

            // org.junit.vintage.engine.samples
            // Find entry for parent.
            val parentPackageAuthors = ps.getOrDefault(parent, mutableMapOf())
            // Add every author.
            for (author in pckg.value) {
                // println("Adding author value for $author")
                val occs = parentPackageAuthors.getOrDefault(author.key, 0)
                parentPackageAuthors[author.key] = occs + author.value
                ps[parent] = parentPackageAuthors
            }
        }
    }

    fun get(): SortedMap<Package, Map<Author, Occurences>> {
        return packages.toSortedMap()
    }

    private fun process(commit: Commit) {
        for (file in commit.filenames) {
            if (fileDoesNotExist(file)) {
                continue
            }

            // We're only interested in files with package declaration.
            val pckg = determinePackage(file) ?: continue

            val m = packages.getOrDefault(pckg, mutableMapOf())
            val count = m[commit.author] ?: 0
            m[commit.author] = count + 1
            packages[pckg] = m

            // Sum up to parent packages
            // org.junit.jupiter.api.extension.support

            // find all packages
        }
    }

    private fun determinePackage(file: String): String? {
        if (!(file.endsWith(".java") || file.endsWith(".kt"))) {
            return null
        }

        val pattern = Pattern.compile("\\s*package\\s+((\\w|\\.)+);?\$")
        File(file).readLines().forEach { line ->
            val matcher = pattern.matcher(line)
            if (matcher.matches()) {
                return matcher.group(1)
            }
        }

        return null
    }

    private fun fileDoesNotExist(file: String) = !File(file).exists()
}
