package com.copperleaf.dokka.json.models

import org.json.JSONObject

interface KotlinDocElement {
    val kind: String
    val name: String
    val qualifiedName: String
    val comment: String
    val summaryPos: Int

    fun toJson(indent: Int = 2): String {
        return JSONObject(this).toString(indent)
    }
}

interface KotlinClasslike : KotlinDocElement

interface KotlinMemberlike : KotlinDocElement {
    val modifiers: List<String>
}