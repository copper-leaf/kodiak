package com.copperleaf.groovydoc.json.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON

/**
 * The result of executing Javadoc and transforming the results to JSON.
 */
class GroovydocRootdoc(
    val packages: List<GroovydocPackageDoc>,
    val classes: List<GroovydocClassDoc>
)

@Serializable
data class GroovydocClassDoc(
    @Transient
    val node: Any? = null,

    val name: String
) {
    companion object {
        fun fromJson(json: String): GroovydocClassDoc {
            return JSON.parse(GroovydocClassDoc.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(GroovydocClassDoc.serializer(), this)
    }
}

@Serializable
data class GroovydocPackageDoc(
    @Transient
    val node: Any? = null,

    val name: String
) {
    companion object {
        fun fromJson(json: String): GroovydocPackageDoc {
            return JSON.parse(GroovydocPackageDoc.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(GroovydocPackageDoc.serializer(), this)
    }
}
