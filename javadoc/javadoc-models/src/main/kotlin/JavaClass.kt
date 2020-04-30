package com.copperleaf.kodiak.java.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.CommentComponent
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
 * KDoc commentComponents on the class.
 */
@Serializable
data class JavaClass(
    @Transient
    val node: Any? = null,

    val `package`: String,
    val superclass: String?,
    val interfaces: List<String>,

    override val subKind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val constructors: List<JavaConstructor>,
    val methods: List<JavaMethod>,
    val fields: List<JavaField>,
    override val signature: List<CommentComponent>,

    val enumItems: List<JavaEnumConstant>
) : DocElement, AutoDocument, SpecializedDocElement, TopLevel {

    override val kind = "Class"

    override val parents = listOfNotNull(superclass, *interfaces.toTypedArray())
    override val contexts = listOf(`package`)

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
