package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
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

fun SourceKittenSubstructure.parameterSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(CommentComponent.TEXT, " ${this.name}"))
    list.add(CommentComponent(CommentComponent.PUNCTUATION, ":"))
    list.add(CommentComponent(CommentComponent.TEXT, " ${this.typenameRawValue}", this.typename))

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

fun List<SwiftParameter>.toParameterListSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()
    list.add(CommentComponent(CommentComponent.PUNCTUATION, "("))
    this.forEachIndexed { index, parameter ->
        list.addAll(parameter.signature)

        if (index < this.size - 1) {
            list.add(CommentComponent(CommentComponent.PUNCTUATION, ", "))
        }
    }
    list.add(CommentComponent(CommentComponent.PUNCTUATION, ")"))

    return list
}