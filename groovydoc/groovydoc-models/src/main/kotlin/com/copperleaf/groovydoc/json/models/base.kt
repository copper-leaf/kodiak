package com.copperleaf.groovydoc.json.models

import kotlinx.serialization.Serializable

interface GroovydocDocElement {
    val kind: String
    val name: String
    val qualifiedName: String
    val simpleComment: String
}

interface GroovydocClasslike : GroovydocDocElement

interface GroovydocMemberlike : GroovydocDocElement {
    val modifiers: List<String>
}

interface GroovydocType : GroovydocDocElement {
    val type: String
    val qualifiedType: String
    val signature: List<SignatureComponent>
    val simpleSignature: String
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