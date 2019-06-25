package com.copperleaf.kodiak.common

/**
 * Indicates that this doc element can be serialized to JSON
 */
interface JsonableDocElement : DocElement {
    fun toJson(): String
}