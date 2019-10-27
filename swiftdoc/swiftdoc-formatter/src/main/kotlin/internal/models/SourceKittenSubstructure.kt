package com.copperleaf.kodiak.swift.internal.models

import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.swift.MainArgs
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
data class SourceKittenSubstructure(
    @SerialName("key.offset") val offset: String = "",
    @SerialName("key.length") val length: String = "",

    @SerialName("key.kind") private val kindRawValue: String = "",
    @SerialName("key.name") val name: String = "",
    @SerialName("key.nameoffset") val nameoffset: String = "",
    @SerialName("key.namelength") val namelength: String = "",

    @SerialName("key.typename") val typenameRawValue: String = "",
    @SerialName("key.accessibility") private val accessibilityRawValue: String = "",

    @SerialName("key.doc.comment") val comment: String = "",

    @SerialName("key.diagnostic_stage") val diagnostic_stage: String = "",
    @SerialName("key.bodylength") val bodylength: String = "",
    @SerialName("key.bodyoffset") val bodyoffset: String = "",
    @SerialName("key.setter_accessibility") val setter_accessibility: String = "",

    @SerialName("key.inheritedtypes") val inheritedtypes: List<SourceKittenSubstructure> = emptyList(),
    @SerialName("key.elements") val elements: List<SourceKittenSubstructure> = emptyList(),
    @SerialName("key.attributes") val attributes: List<SourceKittenSubstructure> = emptyList(),
    @SerialName("key.substructure") val substructures: List<SourceKittenSubstructure> = emptyList()
) {

    @Transient
    var sourceFile: String = ""
        set(value) {
            field = value
            substructures.forEach { it.sourceFile = value }
        }

    val accessibility: SwiftAccessibility
        get() = SwiftAccessibility.parse(accessibilityRawValue)

    val kind: SwiftSubstructureKind
        get() = SwiftSubstructureKind.parse(kindRawValue, name)

    val typename: String
        get() = typenameRawValue.trim().removeSuffix("?")

    val nullable: Boolean
        get() = typenameRawValue.trim().endsWith('?')

    fun format(mainArgs: MainArgs, structure: SourceKittenSubstructure): DocElement? {
        return kind.format(mainArgs, this, structure)
    }

    fun findMatch(structure: SourceKittenSubstructure): SourceKittenSubstructure? {
        return structure.substructures.firstOrNull { it.name == this.name }
    }

    companion object {
        fun fromJson(json: String) : SourceKittenSubstructure {
            return Json.nonstrict.parse(serializer(), json)
        }
    }
}
