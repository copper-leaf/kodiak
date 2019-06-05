package com.copperleaf.javadoc.json.models

import com.copperleaf.json.common.AutoDocument
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.DocElement
import com.copperleaf.json.common.fromDocList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc commentComponents on the
 * package. Class definitions only include metadata, but do not include information about their members.
 */
@Serializable
data class JavaPackageDoc(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val id: String,
    override val modifiers: List<String>,
    override val comment: DocComment,

    val classes: List<JavaClassDoc>
) : DocElement, AutoDocument {
    override val kind = "Package"

    @Transient
    override val nodes = listOf(
        fromDocList(::classes)
    )

    companion object {
        fun fromJson(json: String): JavaPackageDoc {
            return JSON.parse(JavaPackageDoc.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(JavaPackageDoc.serializer(), this)
    }
}

