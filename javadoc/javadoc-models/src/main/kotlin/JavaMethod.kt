package com.copperleaf.kodiak.java.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.fromDoc
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a method or function in a class.
 */
@Serializable
data class JavaMethod(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val parameters: List<JavaParameter>,
    val returnValue: JavaReturnType,
    val signature: List<CommentComponent>
) : DocElement, AutoDocument {
    override val kind = "Method"

    @Transient
    override val nodes = listOf(
        fromDocList(::parameters),
        fromDoc(::returnValue)
    )
}
