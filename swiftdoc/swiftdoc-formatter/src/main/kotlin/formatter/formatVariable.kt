package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
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

fun SourceKittenSubstructure.variableSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(TEXT, "var"))
    list.add(CommentComponent(TEXT, " ${this.name}"))
    list.add(CommentComponent(PUNCTUATION, ":"))
    list.add(CommentComponent(TYPE_NAME, " ${this.typenameRawValue}", this.typename))

    return list
}
