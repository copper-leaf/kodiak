package com.copperleaf.dokka.json.models

import com.copperleaf.json.common.CommentComponent

interface KotlinDocElement {
    val kind: String
    val name: String
    val id: String
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
    val signature: List<CommentComponent>
    val simpleSignature: String
}
