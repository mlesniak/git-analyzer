package com.mlesniak.git.analyzer.source

import java.time.Instant

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

