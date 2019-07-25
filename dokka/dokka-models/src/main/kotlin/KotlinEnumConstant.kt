package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a single enum constant withing an enum class.
 */
@Serializable
data class KotlinEnumConstant(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,
    val signature: List<CommentComponent>
) : DocElement {

    override val kind: String = "enumConstant"
}