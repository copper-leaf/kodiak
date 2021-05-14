package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind
import com.copperleaf.kodiak.swift.models.SwiftConstructor
import com.copperleaf.kodiak.swift.models.SwiftParameter

fun SourceKittenSubstructure.toInitializerDoc(structure: SourceKittenSubstructure): SwiftConstructor {
    val ownStructure = this.findMatch(structure)

    val methodName = this.name.split("(").first().trim()
    val modifiers = this.getModifiers()
    val parameters = ownStructure
        ?.childrenOfType(SwiftSubstructureKind.PARAMETER) { it.toParameterDoc() }
        ?: emptyList()

    return SwiftConstructor(
        this,
        methodName,
        this.name,
        modifiers,
        this.getComment(),
        parameters,
        this.initializerSignature(
            methodName,
            modifiers,
            parameters
        )
    )
}

fun SourceKittenSubstructure.initializerSignature(
    methodName: String,
    modifiers: List<String>,
    parameters: List<SwiftParameter>
): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(RichTextComponent(TEXT, "func"))
    list.add(RichTextComponent(TYPE_NAME, " $methodName"))
    list.addAll(parameters.toParameterListSignature())

    return list
}
