package com.copperleaf.kodiak.swift.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.JsonableDocElement
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.SpecializedDocElement
import com.copperleaf.kodiak.common.TopLevel
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc RichTextComponents on the class.
 */
@Serializable
data class SwiftClass(
    @Transient
    val node: Any? = null,

    val sourceFile: String,
    val superclass: RichTextComponent?,
    val protocols: List<RichTextComponent>,

    override val subKind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val constructors: List<SwiftConstructor>,
    val methods: List<SwiftMethod>,
    val fields: List<SwiftField>,
    override val signature: List<RichTextComponent>
) : JsonableDocElement, AutoDocument, SpecializedDocElement, TopLevel {

    override val kind = "Class"

    override val parents: List<RichTextComponent>
        get() = listOfNotNull(superclass, *protocols.toTypedArray())

    override val contexts: List<RichTextComponent>
        get() = listOf(RichTextComponent(TYPE_NAME, sourceFile, sourceFile))

    override val nodes: List<AutoDocumentNode>
        get() = listOf(
            fromDocList(::fields),
            fromDocList(::constructors),
            fromDocList(::methods)
        )

    override fun toJson(json: Json): String {
        return json.encodeToString(serializer(), this)
    }
}
