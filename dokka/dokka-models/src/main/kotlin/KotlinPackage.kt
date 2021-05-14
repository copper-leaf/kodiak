package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.JsonableDocElement
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.TopLevel
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc comment on the
 * package. Class definitions only include metadata, but do not include information about their members.
 */
@Serializable
data class KotlinPackage(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    val parent: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val classes: List<KotlinClass>,
    val subpackages: List<KotlinPackage>,
    val methods: List<KotlinMethod>,
    val fields: List<KotlinField>,
    val typealiases: List<KotlinTypealias>,
    override val signature: List<RichTextComponent>
) : DocElement, AutoDocument, TopLevel, JsonableDocElement {
    override val kind = "Package"

    override val parents: List<RichTextComponent>
        get() = listOf(RichTextComponent(TYPE_NAME, parent, parent))

    override val contexts: List<RichTextComponent>
        get() = emptyList<RichTextComponent>()

    override val nodes: List<AutoDocumentNode>
        get() = listOf(
            fromDocList(::classes),
            fromDocList(::subpackages),
            fromDocList(::typealiases),
            fromDocList(::fields),
            fromDocList(::methods)
        )

    companion object {
        fun fromJson(json: String): KotlinPackage {
            return Json.decodeFromString(KotlinPackage.serializer(), json)
        }
    }

    override fun toJson(json: Json): String {
        return json.encodeToString(serializer(), this)
    }
}
