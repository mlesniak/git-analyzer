package analyzer

import org.junit.jupiter.api.Test

internal class DomainExpertsTest {
    @Test
    fun `analyze bug`() {
        val packages = mutableMapOf(
            "a.b.both" to mutableMapOf(
                "111" to 2,
                "222" to 3,
            ),
            "a.b.1only" to mutableMapOf(
                "111" to 5,
            ),
            "a.b.2only" to mutableMapOf(
                "222" to 7,
            )
        )

        val ps = extendPackages(packages)

        debugOutput(ps)
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
        println("\nPackage is ${pckg.key}")
        val parts = pckg.key.split(".")
        // org.junit.platform.suite.engine.testsuites
        for (i in 1..parts.size) {
            val parent = parts.take(i).joinToString(separator = ".")
            println("\nCurrent package is $parent")

            // org.junit.vintage.engine.samples
            // Find entry for parent.
            val parentPackageAuthors = ps.getOrDefault(parent, mutableMapOf())
            // Add every author.
            for (author in pckg.value) {
                println("Adding author value for $author")
                val occs = parentPackageAuthors.getOrDefault(author.key, 0)
                parentPackageAuthors[author.key] = occs + author.value
                ps[parent] = parentPackageAuthors
            }
        }
    }

    private fun debugOutput(ps: MutableMap<String, MutableMap<String, Int>>) {
        ps.forEach { pckg ->
            println("\n${pckg.key}")
            for (author in pckg.value) {
                println("  ${author.key}: ${author.value}")
            }
        }
    }
}