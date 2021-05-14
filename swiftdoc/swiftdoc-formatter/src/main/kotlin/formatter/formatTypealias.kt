package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.models.SwiftTypealias

fun SourceKittenSubstructure.toTypealiasDoc(structure: SourceKittenSubstructure): SwiftTypealias {
    return SwiftTypealias(
        this,
        this.kind.name,
        "$sourceFile/${this.name}",
        this.getModifiers(),
        this.getComment(),
        typealiasSignature()
    )
}

fun SourceKittenSubstructure.typealiasSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.add(RichTextComponent(TEXT, this.kind.kindName))
    list.add(RichTextComponent(TYPE_NAME, " ${this.name}", this.name))

    if (this.inheritedtypes.isNotEmpty()) {
        list.add(RichTextComponent(PUNCTUATION, ":"))

        this.inheritedtypes.forEachIndexed { index, type ->
            list.add(RichTextComponent(TYPE_NAME, " ${type.name}", type.name))

            if (index < this.inheritedtypes.size - 1) {
                list.add(RichTextComponent(PUNCTUATION, ", "))
            }
        }
    }

    return list
}
