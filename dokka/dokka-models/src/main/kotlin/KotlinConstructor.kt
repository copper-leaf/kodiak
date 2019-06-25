package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
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
    val signature: List<CommentComponent>
) : DocElement, AutoDocument {
    override val kind = "Constructor"

    @Transient
    override val nodes = listOf(
        fromDocList(::parameters)
    )
}
