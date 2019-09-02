package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
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

    list.add(CommentComponent(CommentComponent.TEXT, "var"))
    list.add(CommentComponent(CommentComponent.TEXT, " ${this.name}"))
    list.add(CommentComponent(CommentComponent.PUNCTUATION, ":"))
    list.add(CommentComponent(CommentComponent.TEXT, " ${this.typenameRawValue}", this.typename))

    return list
}
