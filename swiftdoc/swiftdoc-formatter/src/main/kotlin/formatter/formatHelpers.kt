package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.swift.MainArgs
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind

fun SourceKittenSubstructure.isSuppressed(mainArgs: MainArgs): Boolean {
    return !isVisible(mainArgs) || this.getComment().components.any { it.text.contains("^.*?- suppress".toRegex(RegexOption.MULTILINE)) }
}

fun SourceKittenSubstructure.isVisible(mainArgs: MainArgs): Boolean {
    return this.accessibility in mainArgs.visibility
}

fun SourceKittenSubstructure.getModifiers(): List<String> {
    return this.attributes.map { it.name }
}

fun <T> SourceKittenSubstructure.childrenOfType(
    vararg kind: SwiftSubstructureKind,
    extraFilter: (SourceKittenSubstructure) -> Boolean = { true },
    mapper: (SourceKittenSubstructure) -> T
): List<T> {
    return substructures.filter { it.kind in kind }.filter(extraFilter).map(mapper)
}

fun List<String>.toModifierListSignature(): List<RichTextComponent> {
    return this.map { RichTextComponent(TEXT, "$it ") }
}
