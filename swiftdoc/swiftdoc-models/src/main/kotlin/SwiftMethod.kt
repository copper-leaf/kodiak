package com.copperleaf.kodiak.swift.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.SpecializedDocElement
import com.copperleaf.kodiak.common.fromDoc
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a method or function in a class.
 */
@Serializable
data class SwiftMethod(
    @Transient
    val node: Any? = null,

    override val subKind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val parameters: List<SwiftParameter>,
    val returnValue: SwiftReturnType,
    override val signature: List<RichTextComponent>
) : DocElement, AutoDocument, SpecializedDocElement {

    override val kind = "Method"

    @Transient
    override val nodes = listOf(
        fromDocList(::parameters),
        fromDoc(::returnValue)
    )
}
