package com.copperleaf.kodiak.swift.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.SpecializedDocElement
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc RichTextComponents on the class.
 */
@Serializable
data class SwiftExtension(
    @Transient
    val node: Any? = null,

    val sourceFile: String,
    override val subKind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val methods: List<SwiftMethod>,
    val fields: List<SwiftField>,
    override val signature: List<RichTextComponent>
) : DocElement, AutoDocument, SpecializedDocElement {

    override val kind = "Extension"

    @Transient
    override val nodes = listOf(
        fromDocList(::fields),
        fromDocList(::methods)
    )
}
