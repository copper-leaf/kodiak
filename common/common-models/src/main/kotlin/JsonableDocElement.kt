package com.copperleaf.json.common

/**
 * Indicates that this doc element can be serialized to JSON
 */
interface JsonableDocElement : DocElement {
    fun toJson(): String
}