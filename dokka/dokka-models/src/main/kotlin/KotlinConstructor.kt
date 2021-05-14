package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a constructor of a class.
 */
@Serializable
data class KotlinConstructor(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val parameters: List<KotlinParameter>,
    override val signature: List<RichTextComponent>
) : DocElement, AutoDocument {
    override val kind = "Constructor"

    override val nodes: List<AutoDocumentNode>
        get() = listOf(
            fromDocList(::parameters)
        )
}
