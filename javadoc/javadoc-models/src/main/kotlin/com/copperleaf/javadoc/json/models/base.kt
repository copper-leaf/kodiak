package com.copperleaf.javadoc.json.models

import com.copperleaf.json.common.DocElement

interface JavaClasslike : DocElement

interface JavaMemberlike : DocElement {
    val modifiers: List<String>
}

