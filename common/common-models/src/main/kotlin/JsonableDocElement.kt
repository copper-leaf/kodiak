package com.copperleaf.kodiak.common

import kotlinx.serialization.json.Json

/**
 * Indicates that this doc element can be serialized to JSON
 */
interface JsonableDocElement : DocElement {
    fun toJson(json: Json): String
}
