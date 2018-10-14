package com.copperleaf.javadoc.json.models

import kotlinx.serialization.Serializable

interface JavaDocElement {
    val kind: String
    val name: String
    val qualifiedName: String
    val simpleComment: String
    val comment: List<CommentTag>
    val tags: Map<String, CommentTag>
}

interface JavaClasslike : JavaDocElement

interface JavaMemberlike : JavaDocElement {
    val modifiers: List<String>
}

interface JavaType : JavaDocElement {
    val type: String
    val qualifiedType: String
    val signature: List<SignatureComponent>
    val simpleSignature: String
}

@Serializable
data class CommentTag(
        val kind: String,
        val text: String,
        val className: String
)

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
