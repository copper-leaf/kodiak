package com.copperleaf.dokka.json.models

import com.copperleaf.json.common.AutoDocument
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.DocElement
import com.copperleaf.json.common.fromDoc
import com.copperleaf.json.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a method or function in a class.
 */
@Serializable
data class KotlinMethod(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val receiver: KotlinReceiver? = null,
    val parameters: List<KotlinParameter>,
    val returnValue: KotlinReturnType,
    val signature: List<CommentComponent>
) : DocElement, AutoDocument {
    override val kind = "Method"

    @Transient
    override val nodes = listOf(
        fromDoc(::receiver),
        fromDocList(::parameters),
        fromDoc(::returnValue)
    )
}
