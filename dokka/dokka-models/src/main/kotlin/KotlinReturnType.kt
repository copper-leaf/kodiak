package com.copperleaf.dokka.json.models

import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.ElementType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a method return typeName
 */
@Serializable
data class KotlinReturnType(
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
    override val kind = "ReturnType"
}
