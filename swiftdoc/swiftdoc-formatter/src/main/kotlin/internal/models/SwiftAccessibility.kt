package com.copperleaf.kodiak.swift.internal.models

enum class SwiftAccessibility {
    OPEN,
    PUBLIC,
    INTERNAL,
    FILE_PRIVATE,
    PRIVATE;

    companion object {
        fun parse(input: String): SwiftAccessibility {
            return when {
                input == "source.lang.swift.accessibility.open" -> OPEN
                input == "source.lang.swift.accessibility.public" -> PUBLIC
                input == "source.lang.swift.accessibility.internal" -> INTERNAL
                input == "source.lang.swift.accessibility.file-private" -> FILE_PRIVATE
                input == "source.lang.swift.accessibility.private" -> PRIVATE
                else -> throw Exception(
                    "Unexpected accessibility kind [$input]"
                )
            }
        }
    }
}
