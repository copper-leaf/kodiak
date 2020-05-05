package com.copperleaf.kodiak.java.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.SpecializedDocElement
import com.copperleaf.kodiak.common.TopLevel
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc RichTextComponents on the class.
 */
@Serializable
data class JavaClass(
    @Transient
    val node: Any? = null,

    val `package`: String,
    val superclass: RichTextComponent?,
    val interfaces: List<RichTextComponent>,

    override val subKind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val constructors: List<JavaConstructor>,
    val methods: List<JavaMethod>,
    val fields: List<JavaField>,
    override val signature: List<RichTextComponent>,

    val enumItems: List<JavaEnumConstant>
) : DocElement, AutoDocument, SpecializedDocElement, TopLevel {

    override val kind = "Class"

    override val parents = listOfNotNull(superclass, *interfaces.toTypedArray())
    override val contexts = listOf(RichTextComponent(TYPE_NAME, `package`, `package`))

    @Transient
    override val nodes = listOf(
        fromDocList(::enumItems),
        fromDocList(::fields),
        fromDocList(::constructors),
        fromDocList(::methods)
    )

    @UseExperimental(UnstableDefault::class)
    fun toJson(): String {
        return Json.indented.stringify(serializer(), this)
    }
}
