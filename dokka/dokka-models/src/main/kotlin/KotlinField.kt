package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.ElementType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a field or property in a class.
 */
@Serializable
data class KotlinField(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    override val typeName: String,
    override val typeId: String,
    override val signature: List<RichTextComponent>
) : ElementType, AutoDocument {
    override val kind = "Field"

    @Transient
    override val nodes = emptyList<AutoDocumentNode>()
}
