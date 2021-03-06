package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.MainArgs
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.CLASS_METHOD
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.CLASS_VARIABLE
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.INIT_METHOD
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.INSTANCE_METHOD
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.INSTANCE_VARIABLE
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.STATIC_METHOD
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind.STATIC_VARIABLE
import com.copperleaf.kodiak.swift.models.SwiftClass

fun SourceKittenSubstructure.toClassDoc(
    mainArgs: MainArgs,
    structure: SourceKittenSubstructure,
    deep: Boolean = false
): SwiftClass {
    return SwiftClass(
        this,
        sourceFile,
        null,
        emptyList(),
        this.kind.name,
        this.name,
        "$sourceFile/${this.name}",
        this.getModifiers(),
        this.getComment(),
        if (deep) {
            this
                .childrenOfType(
                    INIT_METHOD,
                    extraFilter = { !it.isSuppressed(mainArgs) }
                ) { it.toInitializerDoc(structure) }
        } else {
            emptyList()
        },
        if (deep) {
            this
                .childrenOfType(
                    STATIC_METHOD, CLASS_METHOD, INSTANCE_METHOD,
                    extraFilter = { !it.isSuppressed(mainArgs) }
                ) { it.toFunctionDoc(structure) }
        } else {
            emptyList()
        },
        if (deep) {
            this
                .childrenOfType(
                    STATIC_VARIABLE, CLASS_VARIABLE, INSTANCE_VARIABLE,
                    extraFilter = { !it.isSuppressed(mainArgs) }
                ) { it.toVariableDoc(structure) }
        } else {
            emptyList()
        },
        classSignature()
    )
}

fun SourceKittenSubstructure.toEnumCaseDoc(mainArgs: MainArgs, structure: SourceKittenSubstructure): SwiftClass {
    return this.toClassDoc(mainArgs, structure, false)
}

fun SourceKittenSubstructure.classSignature(): List<RichTextComponent> {
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
