package com.copperleaf.kodiak.swift.internal.models

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.map

data class SourceKittenParseResult(
    val files: Map<String, SourceKittenFile>
) {

    @UseExperimental(UnstableDefault::class)
    companion object {
        fun fromJson(json: String): SourceKittenParseResult {
            val filesSerializer = (StringSerializer to SourceKittenFile.serializer()).map
            val files = Json.nonstrict.parse(filesSerializer, json)
            return SourceKittenParseResult(files)
        }
    }
}
