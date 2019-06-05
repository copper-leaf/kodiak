package com.copperleaf.dokka.json.models

import com.copperleaf.json.common.AutoDocument
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.DocElement
import com.copperleaf.json.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc comment on the class.
 */
@Serializable
data class KotlinClass(
    @Transient
    val node: Any? = null,

    val `package`: String,
    override val kind: String,
    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val constructors: List<KotlinConstructor>,
    val methods: List<KotlinMethod>,
    val fields: List<KotlinField>,
    val extensions: List<KotlinMethod>,
    val signature: List<CommentComponent>
) : DocElement, AutoDocument {

    @Transient
    override val nodes = listOf(
        fromDocList(::fields),
        fromDocList(::constructors),
        fromDocList(::methods),
        fromDocList(::extensions)
    )

    companion object {
        fun fromJson(json: String): KotlinClass {
            return JSON.parse(KotlinClass.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(KotlinClass.serializer(), this)
    }
}
