package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind

fun SourceKittenSubstructure.getModifiers(): List<String> {
    return this.attributes.map { it.name }
}

fun <T> SourceKittenSubstructure.childrenOfType(vararg kind: SwiftSubstructureKind, mapper: (SourceKittenSubstructure) -> T): List<T> {
    return substructures.filter { it.kind in kind }.map(mapper)
}

fun List<String>.toModifierListSignature(): List<CommentComponent> {
    return this.map { CommentComponent("modifier", "$it ") }
}
