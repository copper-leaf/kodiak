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
 * The docs for a constructor of a class.
 */
@Serializable
data class JavaConstructor(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val parameters: List<JavaParameter>,
    val signature: List<CommentComponent>
) : DocElement, AutoDocument {
    override val kind = "Constructor"

    @Transient
    override val nodes = listOf(
        fromDocList(::parameters)
    )
}
