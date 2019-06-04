package com.copperleaf.javadoc.json.models

import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocElement

interface JavaClasslike : DocElement

interface JavaMemberlike : DocElement {
    val modifiers: List<String>
}

interface JavaType : DocElement {
    val type: String
    val qualifiedType: String
    val signature: List<CommentComponent>
    val simpleSignature: String
}