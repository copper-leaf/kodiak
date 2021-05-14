package com.copperleaf.kodiak.swift.internal.models

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

data class SourceKittenParseResult(
    val files: Map<String, SourceKittenFile>
) {

    companion object {
        fun fromJson(jsonModule: Json, json: String): SourceKittenParseResult {
            val filesSerializer = MapSerializer(String.serializer(), SourceKittenFile.serializer())
            val files = jsonModule.decodeFromString(filesSerializer, json)
            return SourceKittenParseResult(files)
        }
    }
}
