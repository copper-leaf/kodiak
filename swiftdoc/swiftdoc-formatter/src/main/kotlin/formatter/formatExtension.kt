package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
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
        "${sourceFile}/${this.name}",
        this.getModifiers(),
        this.getComment(),
        this.childrenOfType(STATIC_METHOD, extraFilter = { !it.isSuppressed(mainArgs) }) { it.toFunctionDoc(structure) },
        this.childrenOfType(STATIC_VARIABLE, extraFilter = { !it.isSuppressed(mainArgs) }) { it.toVariableDoc(structure) },
        extensionSignature()
    )
}

fun SourceKittenSubstructure.extensionSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(TEXT, "extension"))
    list.add(CommentComponent(TEXT, " ${this.kind.kindName}"))
    list.add(CommentComponent(TYPE_NAME, " ${this.name}", this.name))

    if(this.inheritedtypes.isNotEmpty()) {
        list.add(CommentComponent(PUNCTUATION, ":"))

        this.inheritedtypes.forEachIndexed { index, type ->
            list.add(CommentComponent(TYPE_NAME, " ${type.name}", type.name))

            if (index < this.inheritedtypes.size - 1) {
                list.add(CommentComponent(PUNCTUATION, ", "))
            }
        }
    }

    return list
}
