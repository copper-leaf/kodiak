package com.copperleaf.kodiak.swift.internal.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SourceKittenFile(
    @SerialName("key.substructure") val substructures: List<SourceKittenSubstructure> = emptyList()
) {
    @Transient
    var sourceFile: String = ""
        set(value) {
            field = value
            substructures.forEach { it.sourceFile = value }
        }
}
