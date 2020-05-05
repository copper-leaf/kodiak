package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.models.SwiftField

fun SourceKittenSubstructure.toVariableDoc(structure: SourceKittenSubstructure): SwiftField {
    return SwiftField(
        this,
        this.name,
        this.name,
        this.getModifiers(),
        this.getComment(),
        this.typenameRawValue,
        this.typename,
        this.variableSignature()
    )
}

fun SourceKittenSubstructure.variableSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.add(RichTextComponent(TEXT, "var"))
    list.add(RichTextComponent(TEXT, " ${this.name}"))
    list.add(RichTextComponent(PUNCTUATION, ":"))
    list.add(RichTextComponent(TYPE_NAME, " ${this.typenameRawValue}", this.typename))

    return list
}
