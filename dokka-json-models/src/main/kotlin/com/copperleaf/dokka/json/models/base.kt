package com.copperleaf.dokka.json.models

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