package com.copperleaf.kodiak.swift.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.JsonableDocElement
import com.copperleaf.kodiak.common.SpecializedDocElement
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
data class SwiftClass(
    @Transient
    val node: Any? = null,

    val sourceFile: String,
    override val subKind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val constructors: List<SwiftConstructor>,
    val methods: List<SwiftMethod>,
    val fields: List<SwiftField>,
    override val signature: List<CommentComponent>
) : DocElement, AutoDocument, SpecializedDocElement, JsonableDocElement {

    override val kind = "Class"

    @Transient
    override val nodes = listOf(
        fromDocList(::fields),
        fromDocList(::constructors),
        fromDocList(::methods)
    )

    @UseExperimental(UnstableDefault::class)
    override fun toJson(): String {
        return Json.indented.stringify(serializer(), this)
    }
}
