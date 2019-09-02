package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.models.SwiftTypealias

fun SourceKittenSubstructure.toTypealiasDoc(structure: SourceKittenSubstructure): SwiftTypealias {
    return SwiftTypealias(
        this,
        this.kind.name,
        "${sourceFile}/${this.name}",
        this.getModifiers(),
        this.getComment(),
        typealiasSignature()
    )
}

fun SourceKittenSubstructure.typealiasSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(TEXT, this.kind.kindName))
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
