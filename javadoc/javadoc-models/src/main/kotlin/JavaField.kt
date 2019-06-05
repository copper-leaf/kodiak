package com.copperleaf.javadoc.json.models

import com.copperleaf.json.common.AutoDocument
import com.copperleaf.json.common.AutoDocumentNode
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.DocElement
import com.copperleaf.json.common.ElementType
import com.copperleaf.json.common.fromDoc
import com.copperleaf.json.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a field or property in a class.
 */
@Serializable
data class JavaField(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    override val typeName: String,
    override val typeId: String,
    override val signature: List<CommentComponent>
) : DocElement, ElementType, AutoDocument {
    override val kind = "Field"

    @Transient
    override val nodes = emptyList<AutoDocumentNode>()
}
