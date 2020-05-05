package com.copperleaf.kodiak.java.models

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a single enum constant withing an enum class.
 */
@Serializable
data class JavaEnumConstant(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,
    override val signature: List<RichTextComponent>
) : DocElement {

    override val kind: String = "enumConstant"
}
