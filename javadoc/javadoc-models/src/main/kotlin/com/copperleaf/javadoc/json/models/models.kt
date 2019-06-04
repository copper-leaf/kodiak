package com.copperleaf.javadoc.json.models

import com.copperleaf.json.common.CommentComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON

/**
 * The result of executing Javadoc and transforming the results to JSON.
 */
class JavadocRootdoc(
        val packages: List<JavaPackageDoc>,
        val classes: List<JavaClassDoc>
)

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc comment on the class.
 */
@Serializable
data class JavaClassDoc(
    @Transient
        val node: Any? = null,

    val `package`: String,
    val modifiers: List<String>,
    override val kind: String,
    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,
    override val comment: List<CommentComponent>,
    override val tags: Map<String, CommentComponent>,
    val constructors: List<JavaConstructor>,
    val methods: List<JavaMethod>,
    val fields: List<JavaField>,
    val signature: List<CommentComponent>,
    val simpleSignature: String = signature.map { it.text }.joinToString("")
) : JavaClasslike {
    companion object {
        fun fromJson(json: String): JavaClassDoc {
            return JSON.parse(JavaClassDoc.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(JavaClassDoc.serializer(), this)
    }
}

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc comment on the
 * package. Class definitions only include metadata, but do not include information about their members.
 */
@Serializable
data class JavaPackageDoc(
    @Transient
        val node: Any? = null,

    override val name: String,
    override val qualifiedName: String,
    override val simpleComment: String,
    override val comment: List<CommentComponent>,
    override val tags: Map<String, CommentComponent>,
    val classes: List<JavaClassDoc>
) : JavaDocElement {
    override val kind = "Package"

    companion object {
        fun fromJson(json: String): JavaPackageDoc {
            return JSON.parse(JavaPackageDoc.serializer(), json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(JavaPackageDoc.serializer(), this)
    }
}

/**
 * The docs for a constructor of a class.
 */
@Serializable
data class JavaConstructor(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentComponent>,
        override val tags: Map<String, CommentComponent>,
        override val modifiers: List<String>,
        val parameters: List<JavaParameter>,
        val signature: List<CommentComponent>,
        val simpleSignature: String = signature.map { it.text }.joinToString("")
) : JavaMemberlike {
    override val kind = "Constructor"
}

/**
 * The docs for a method or function in a class.
 */
@Serializable
data class JavaMethod(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentComponent>,
        override val tags: Map<String, CommentComponent>,
        override val modifiers: List<String>,
        val parameters: List<JavaParameter>,
        val returnValue: JavaReturnType,
        val signature: List<CommentComponent>,
        val simpleSignature: String = signature.map { it.text }.joinToString("")
) : JavaMemberlike {
    override val kind = "Method"
}

/**
 * The docs for a field or property in a class.
 */
@Serializable
data class JavaField(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentComponent>,
        override val tags: Map<String, CommentComponent>,
        override val modifiers: List<String>,

        override val type: String,
        override val qualifiedType: String,
        override val signature: List<CommentComponent>,
        override val simpleSignature: String = signature.map { it.text }.joinToString("")
) : JavaMemberlike, JavaType {
    override val kind = "Field"
}

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class JavaParameter(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentComponent>,
        override val tags: Map<String, CommentComponent>,

        override val type: String,
        override val qualifiedType: String,
        override val signature: List<CommentComponent>,
        override val simpleSignature: String = signature.map { it.text }.joinToString("")
) : JavaDocElement, JavaType {
    override val kind = "Parameter"
}

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class JavaReturnType(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentComponent>,
        override val tags: Map<String, CommentComponent>,

        override val type: String,
        override val qualifiedType: String,
        override val signature: List<CommentComponent>,
        override val simpleSignature: String = signature.map { it.text }.joinToString("")
) : JavaDocElement, JavaType {
    override val kind = "ReturnValue"
}
