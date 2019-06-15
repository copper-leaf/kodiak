package com.copperleaf.groovydoc.json.models

import com.copperleaf.json.common.AutoDocument
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.DocElement
import com.copperleaf.json.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc comment on the class.
 */
@Serializable
data class GroovyClass(
    @Transient
    val node: Any? = null,

    val `package`: String,
    override val kind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val constructors: List<GroovyConstructor>,
    val methods: List<GroovyMethod>,
    val fields: List<GroovyField>,
    val signature: List<CommentComponent>
) : DocElement, AutoDocument {

    @Transient
    override val nodes = listOf(
        fromDocList(::fields),
        fromDocList(::constructors),
        fromDocList(::methods)
    )

    @UseExperimental(UnstableDefault::class)
    fun toJson(): String {
        return Json.indented.stringify(GroovyClass.serializer(), this)
    }
}
