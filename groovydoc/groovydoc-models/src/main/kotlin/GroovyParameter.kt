package com.copperleaf.kodiak.groovy.models

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.ElementType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class GroovyParameter(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    override val typeName: String,
    override val typeId: String,
    override val signature: List<RichTextComponent>
) : ElementType {
    override val kind = "Parameter"
}
