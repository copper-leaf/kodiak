package com.copperleaf.javadoc.json.models

import kotlinx.serialization.Serializable

interface JavadocDocElement {
    val kind: String
    val name: String
    val qualifiedName: String
    val simpleComment: String
    val comment: List<CommentTag>
    val tags: Map<String, CommentTag>
}

interface JavadocClasslike : JavadocDocElement

interface JavadocMemberlike : JavadocDocElement {
    val modifiers: List<String>
}

/**
 * A component to the rich signature. The complete signature can be created by joining all components together,
 * optionally generating
 */
@Serializable
data class CommentTag(
        val kind: String,
        val text: String,
        val className: String
)