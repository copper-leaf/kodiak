package com.copperleaf.javadoc.json.models

import com.copperleaf.json.common.CommentComponent

interface JavaDocElement {
    val kind: String
    val name: String
    val qualifiedName: String
    val simpleComment: String
    val comment: List<CommentComponent>
    val tags: Map<String, CommentComponent>
}

interface JavaClasslike : JavaDocElement

interface JavaMemberlike : JavaDocElement {
    val modifiers: List<String>
}

interface JavaType : JavaDocElement {
    val type: String
    val qualifiedType: String
    val signature: List<CommentComponent>
    val simpleSignature: String
}