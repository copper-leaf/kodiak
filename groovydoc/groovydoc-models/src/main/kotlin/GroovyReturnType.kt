package com.copperleaf.groovydoc.json.models

import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.ElementType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class GroovyReturnType(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    override val typeName: String,
    override val typeId: String,
    override val signature: List<CommentComponent>
) : ElementType {
    override val kind = "ReturnValue"
}
