package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.MainArgs
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.STATIC_METHOD
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.STATIC_VARIABLE
import com.copperleaf.kodiak.swift.models.SwiftExtension

fun SourceKittenSubstructure.toExtensionDoc(mainArgs: MainArgs, structure: SourceKittenSubstructure): SwiftExtension {
    return SwiftExtension(
        this,
        sourceFile,
        this.kind.name,
        this.name,
        "$sourceFile/${this.name}",
        this.getModifiers(),
        this.getComment(),
        this.childrenOfType(
            STATIC_METHOD,
            extraFilter = { !it.isSuppressed(mainArgs) }
        ) { it.toFunctionDoc(structure) },
        this.childrenOfType(
            STATIC_VARIABLE,
            extraFilter = { !it.isSuppressed(mainArgs) }
        ) { it.toVariableDoc(structure) },
        extensionSignature()
    )
}

fun SourceKittenSubstructure.extensionSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.add(RichTextComponent(TEXT, "extension"))
    list.add(RichTextComponent(TEXT, " ${this.kind.kindName}"))
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
