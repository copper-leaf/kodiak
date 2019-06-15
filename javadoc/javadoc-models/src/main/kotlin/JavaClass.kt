package com.copperleaf.javadoc.json.models

import com.copperleaf.json.common.AutoDocument
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.DocElement
import com.copperleaf.json.common.SpecializedDocElement
import com.copperleaf.json.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc commentComponents on the class.
 */
@Serializable
data class JavaClass(
    @Transient
    val node: Any? = null,

    val `package`: String,
    override val subKind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val constructors: List<JavaConstructor>,
    val methods: List<JavaMethod>,
    val fields: List<JavaField>,
    val signature: List<CommentComponent>
) : DocElement, AutoDocument, SpecializedDocElement {

    override val kind = "Class"

    @Transient
    override val nodes = listOf(
        fromDocList(::fields),
        fromDocList(::constructors),
        fromDocList(::methods)
    )

    @UseExperimental(UnstableDefault::class)
    fun toJson(): String {
        return Json.indented.stringify(serializer(), this)
    }
}
