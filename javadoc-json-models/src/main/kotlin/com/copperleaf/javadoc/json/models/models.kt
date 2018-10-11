package com.copperleaf.javadoc.json.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON

/**
 * The result of executing Javadoc and transforming the results to JSON.
 */
class JavadocRootdoc(
        val packages: List<JavadocPackageDoc>,
        val classes: List<JavadocClassDoc>
)

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc comment on the class.
 */
@Serializable
data class JavadocClassDoc(
        @Transient
        val node: Any? = null,

        val `package`: String,
        override val kind: String,
        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentTag>,
        override val tags: Map<String, CommentTag>,
        val constructors: List<JavadocConstructor>,
        val methods: List<JavadocMethod>,
        val fields: List<JavadocField>
) : JavadocClasslike {
    companion object {
        fun fromJson(json: String): JavadocClassDoc {
            return JSON.parse(json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(this)
    }
}

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc comment on the
 * package. Class definitions only include metadata, but do not include information about their members.
 */
@Serializable
data class JavadocPackageDoc(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentTag>,
        override val tags: Map<String, CommentTag>,
        val classes: List<JavadocClassDoc>
) : JavadocDocElement {
    override val kind = "Package"

    companion object {
        fun fromJson(json: String): JavadocPackageDoc {
            return JSON.parse(json)
        }
    }

    fun toJson(): String {
        return JSON.indented.stringify(this)
    }
}

/**
 * The docs for a constructor of a class.
 */
@Serializable
data class JavadocConstructor(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentTag>,
        override val tags: Map<String, CommentTag>,
        override val modifiers: List<String>,
        val parameters: List<JavadocParameter>,
        val signature: List<SignatureComponent>,
        val simpleSignature: String = signature.map { it.name }.joinToString("")
) : JavadocMemberlike {
    override val kind = "Constructor"
}

/**
 * The docs for a method or function in a class.
 */
@Serializable
data class JavadocMethod(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentTag>,
        override val tags: Map<String, CommentTag>,
        override val modifiers: List<String>,
        val parameters: List<JavadocParameter>,
        val returnValue: JavadocReturnValue,
        val signature: List<SignatureComponent>,
        val simpleSignature: String = signature.map { it.name }.joinToString("")
) : JavadocMemberlike {
    override val kind = "Method"
}

/**
 * The docs for a field or property in a class.
 */
@Serializable
data class JavadocField(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentTag>,
        override val tags: Map<String, CommentTag>,
        override val modifiers: List<String>,
        val type: String,
        val qualifiedType: String,
        val nullable: Boolean,
        val signature: List<SignatureComponent>,
        val simpleSignature: String = signature.map { it.name }.joinToString("")
) : JavadocMemberlike {
    override val kind = "Field"
}

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class JavadocParameter(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentTag>,
        override val tags: Map<String, CommentTag>,
        val type: String,
        val qualifiedType: String,
        val nullable: Boolean,
        val defaultValue: String?
) : JavadocDocElement {
    override val kind = "Parameter"
}

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class JavadocReturnValue(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val simpleComment: String,
        override val comment: List<CommentTag>,
        override val tags: Map<String, CommentTag>,
        val type: String,
        val nullable: Boolean
) : JavadocDocElement {
    override val kind = "ReturnValue"
}

/**
 * A component to the rich signature. The complete signature can be created by joining all components together,
 * optionally generating
 */
@Serializable
data class SignatureComponent(
        val kind: String,
        val name: String,
        val qualifiedName: String
)
