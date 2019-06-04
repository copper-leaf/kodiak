package com.copperleaf.groovydoc.json.models

import com.copperleaf.json.common.CommentComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON

/**
 * The result of executing Groovydocdoc and transforming the results to JSON.
 */
class GroovydocRootdoc(
    val packages: List<GroovydocPackageDoc>,
    val classes: List<GroovydocClassDoc>
)

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc comment on the class.
 */
@Serializable
data class GroovydocClassDoc(
    @Transient
    val node: Any? = null,

    val `package`: String,
    val modifiers: List<String>,
    override val kind: String,
    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,
    val constructors: List<GroovydocConstructor>,
    val methods: List<GroovydocMethod>,
    val fields: List<GroovydocField>,
    val signature: List<CommentComponent>,
    val simpleSignature: String = signature.map { it.text }.joinToString("")
) : GroovydocClasslike {
    companion object {
        fun fromJson(json: String): GroovydocClassDoc {
            return JSON.parse(GroovydocClassDoc.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(GroovydocClassDoc.serializer(), this)
    }
}

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc comment on the
 * package. Class definitions only include metadata, but do not include information about their members.
 */
@Serializable
data class GroovydocPackageDoc(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,
    val classes: List<GroovydocClassDoc>
) : GroovydocDocElement {
    override val kind = "Package"

    companion object {
        fun fromJson(json: String): GroovydocPackageDoc {
            return JSON.parse(GroovydocPackageDoc.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(GroovydocPackageDoc.serializer(), this)
    }
}

/**
 * The docs for a constructor of a class.
 */
@Serializable
data class GroovydocConstructor(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,
    override val modifiers: List<String>,
    val parameters: List<GroovydocParameter>,
    val signature: List<CommentComponent>,
    val simpleSignature: String = signature.map { it.text }.joinToString("")
) : GroovydocMemberlike {
    override val kind = "Constructor"
}

/**
 * The docs for a method or function in a class.
 */
@Serializable
data class GroovydocMethod(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,
    override val modifiers: List<String>,
    val parameters: List<GroovydocParameter>,
    val returnValue: GroovydocReturnType,
    val signature: List<CommentComponent>,
    val simpleSignature: String = signature.map { it.text }.joinToString("")
) : GroovydocMemberlike {
    override val kind = "Method"
}

/**
 * The docs for a field or property in a class.
 */
@Serializable
data class GroovydocField(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,
    override val modifiers: List<String>,

    override val type: String,
    override val qualifiedType: String,
    override val signature: List<CommentComponent>,
    override val simpleSignature: String = signature.map { it.text }.joinToString("")
) : GroovydocMemberlike, GroovydocType {
    override val kind = "Field"
}

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class GroovydocParameter(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,

    override val type: String,
    override val qualifiedType: String,
    override val signature: List<CommentComponent>,
    override val simpleSignature: String = signature.map { it.text }.joinToString("")
) : GroovydocDocElement, GroovydocType {
    override val kind = "Parameter"
}

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class GroovydocReturnType(
    @Transient
    val node: Any? = null,

    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,

    override val type: String,
    override val qualifiedType: String,
    override val signature: List<CommentComponent>,
    override val simpleSignature: String = signature.map { it.text }.joinToString("")
) : GroovydocDocElement, GroovydocType {
    override val kind = "ReturnValue"
}
