package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.AutoDocument
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
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
    override val modifiers: List<String>,
    override val comment: DocComment,

    val classes: List<KotlinClass>,
    val methods: List<KotlinMethod>,
    val fields: List<KotlinField>
) : DocElement, AutoDocument {
    override val kind = "Package"

    @Transient
    override val nodes = listOf(
        fromDocList(::classes),
        fromDocList(::fields),
        fromDocList(::methods)
    )

    @UseExperimental(UnstableDefault::class)
    companion object {
        fun fromJson(json: String): KotlinPackage {
            return Json.parse(KotlinPackage.serializer(), json)
        }
    }

    @UseExperimental(UnstableDefault::class)
    fun toJson(): String {
        return Json.indented.stringify(KotlinPackage.serializer(), this)
    }
}
