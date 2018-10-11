package com.copperleaf.dokka.json.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JSON

/**
 * The result of executing Dokka and transforming the results to JSON.
 */
class KotlinRootdoc(
        val packages: List<KotlinPackageDoc>,
        val classes: List<KotlinClassDoc>
)

/**
 * The docs for a single class. Includes a list of the constructors, methods, and fields in the class, as well as the
 * KDoc comment on the class.
 */
@Serializable
data class KotlinClassDoc(
        @Transient
        val node: Any? = null,

        val `package`: String,
        override val kind: String,
        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        val constructors: List<KotlinConstructor>,
        val methods: List<KotlinMethod>,
        val fields: List<KotlinField>,
        val extensions: List<KotlinMethod>
) : KotlinClasslike {
    companion object {
        fun fromJson(json: String): KotlinClassDoc {
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
data class KotlinPackageDoc(
        @Transient
        val node: Any? = null,

        val classes: List<KotlinClassDoc>,
        val methods: List<KotlinMethod>,
        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int
) : KotlinDocElement {
    override val kind = "Package"

    companion object {
        fun fromJson(json: String): KotlinPackageDoc {
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
data class KotlinConstructor(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        override val modifiers: List<String>,
        val parameters: List<KotlinParameter>,
        val signature: List<SignatureComponent>,
        val simpleSignature: String = signature.map { it.name }.joinToString("")
) : KotlinMemberlike {
    override val kind = "Constructor"
}

/**
 * The docs for a method or function in a class.
 */
@Serializable
data class KotlinMethod(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        override val modifiers: List<String>,

        val parameters: List<KotlinParameter>,
        val receiver: KotlinReceiverType? = null,
        val returnValue: KotlinReturnType,
        val signature: List<SignatureComponent>,
        val simpleSignature: String = signature.map { it.name }.joinToString("")
) : KotlinMemberlike {
    override val kind = "Method"
}

/**
 * The docs for a field or property in a class.
 */
@Serializable
data class KotlinField(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        override val modifiers: List<String>,

        override val type: String,
        override val qualifiedType: String,
        override val signature: List<SignatureComponent>,
        override val simpleSignature: String = signature.map { it.name }.joinToString("")
) : KotlinMemberlike, KotlinType {
    override val kind = "Field"
}

/**
 * The docs for a parameter of a constructor or method
 */
@Serializable
data class KotlinParameter(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,

        override val type: String,
        override val qualifiedType: String,
        val defaultValue: String?,
        override val signature: List<SignatureComponent>,
        override val simpleSignature: String = signature.map { it.name }.joinToString("")

) : KotlinDocElement, KotlinType {
    override val kind = "Parameter"
}

/**
 * The docs for a method return type
 */
@Serializable
data class KotlinReturnType(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,

        override val type: String,
        override val qualifiedType: String,
        override val signature: List<SignatureComponent>,
        override val simpleSignature: String = signature.map { it.name }.joinToString("")
) : KotlinDocElement, KotlinType {
    override val kind = "ReturnType"
}

/**
 * The docs for a method receiver type
 */
@Serializable
data class KotlinReceiverType(
        @Transient
        val node: Any? = null,

        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,

        override val type: String,
        override val qualifiedType: String,
        override val signature: List<SignatureComponent>,
        override val simpleSignature: String = signature.map { it.name }.joinToString("")
) : KotlinDocElement, KotlinType {
    override val kind = "ReceiverType"
}

/**
 * A component to the rich signature. The complete signature can be created by joining all components together,
 * optionally generating links for individual components of the signature.
 */
@Serializable
data class SignatureComponent(
        val kind: String,
        val name: String,
        val qualifiedName: String
)