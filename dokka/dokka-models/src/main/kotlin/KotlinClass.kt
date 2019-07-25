package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.fromDoc
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

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
    val signature: List<CommentComponent>,

    val companionObject: KotlinClass?,
    val enumItems: List<KotlinEnumConstant>
) : DocElement, AutoDocument {

    @Transient
    override val nodes = listOf(
        fromDocList(::fields),
        fromDocList(::constructors),
        fromDocList(::methods),
        fromDocList(::extensions),
        fromDoc(::companionObject)
    )

    @UseExperimental(UnstableDefault::class)
    companion object {
        fun fromJson(json: String): KotlinClass {
            return Json.parse(KotlinClass.serializer(), json)
        }
    }

    @UseExperimental(UnstableDefault::class)
    fun toJson(): String {
        return Json.indented.stringify(KotlinClass.serializer(), this)
    }
}
