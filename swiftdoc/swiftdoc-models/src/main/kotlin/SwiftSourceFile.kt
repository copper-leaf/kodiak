package com.copperleaf.kodiak.swift.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.TopLevel
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc RichTextComponents on the
 * package. Class definitions only include metadata, but do not include information about their members.
 */
@Serializable
data class SwiftSourceFile(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val classes: List<SwiftClass>,
    val variables: List<SwiftField>,
    val functions: List<SwiftMethod>,
    val typealiases: List<SwiftTypealias>,
    val extensions: List<SwiftExtension>,
    override val signature: List<RichTextComponent>
) : DocElement, AutoDocument, TopLevel {
    override val kind = "SourceFile"

    override val parents = listOf<RichTextComponent>()
    override val contexts = emptyList<RichTextComponent>()

    @Transient
    override val nodes = listOf(
        fromDocList(::classes),
        fromDocList(::variables),
        fromDocList(::functions),
        fromDocList(::typealiases),
        fromDocList(::extensions)
    )

    @UseExperimental(UnstableDefault::class)
    fun toJson(): String {
        return Json.indented.stringify(serializer(), this)
    }
}
