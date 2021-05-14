package com.copperleaf.kodiak.java.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.JsonableDocElement
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.TopLevel
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc RichTextComponents on the
 * package. Class definitions only include metadata, but do not include information about their members.
 */
@Serializable
data class JavaPackage(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    val parent: String,
    override val modifiers: List<String>,
    override val comment: DocComment,
    override val signature: List<RichTextComponent>,
    val classes: List<JavaClass>,
    val subpackages: List<JavaPackage>
) : JsonableDocElement, AutoDocument, TopLevel {
    override val kind = "Package"

    override val parents: List<RichTextComponent>
        get() = listOf(RichTextComponent(TYPE_NAME, parent, parent))

    override val contexts: List<RichTextComponent>
        get() = emptyList<RichTextComponent>()

    override val nodes: List<AutoDocumentNode>
        get() = listOf<AutoDocumentNode>(
            fromDocList(::classes),
            fromDocList(::subpackages)
        )

    override fun toJson(json: Json): String {
        return json.encodeToString(serializer(), this)
    }
}
