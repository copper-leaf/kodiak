package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.models.SwiftParameter

fun SourceKittenSubstructure.toParameterDoc(): SwiftParameter {
    return SwiftParameter(
        this,
        "Parameter",
        this.name,
        this.name,
        this.getModifiers(),
        this.getComment(),
        this.typenameRawValue,
        this.typename,
        this.parameterSignature()
    )
}

fun SourceKittenSubstructure.parameterSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.add(RichTextComponent(TEXT, " ${this.name}"))
    list.add(RichTextComponent(PUNCTUATION, ":"))
    list.add(RichTextComponent(TYPE_NAME, " ${this.typenameRawValue}", this.typename))

    return list
}

fun SourceKittenSubstructure.toTypeParameterDoc(): SwiftParameter {
    return SwiftParameter(
        this,
        "GenericTypeParameter",
        this.name,
        this.name,
        this.getModifiers(),
        this.getComment(),
        this.typename,
        this.typename,
        emptyList()
    )
}

fun List<SwiftParameter>.toParameterListSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()
    list.add(RichTextComponent(PUNCTUATION, "("))
    this.forEachIndexed { index, parameter ->
        list.addAll(parameter.signature)

        if (index < this.size - 1) {
            list.add(RichTextComponent(PUNCTUATION, ", "))
        }
    }
    list.add(RichTextComponent(PUNCTUATION, ")"))

    return list
}
