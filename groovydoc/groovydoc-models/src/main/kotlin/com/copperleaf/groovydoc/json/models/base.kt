package com.copperleaf.groovydoc.json.models

import com.copperleaf.json.common.CommentComponent

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
    val signature: List<CommentComponent>
    val simpleSignature: String
}