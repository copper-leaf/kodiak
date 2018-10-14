package com.copperleaf.dokka.json.models

import kotlinx.serialization.Serializable

interface KotlinDocElement {
    val kind: String
    val name: String
    val qualifiedName: String
    val comment: String
    val summaryPos: Int
}

interface KotlinClasslike : KotlinDocElement

interface KotlinMemberlike : KotlinDocElement {
    val modifiers: List<String>
}

interface KotlinType : KotlinDocElement {
    val type: String
    val qualifiedType: String
    val signature: List<SignatureComponent>
    val simpleSignature: String
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