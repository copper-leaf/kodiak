package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind
import com.copperleaf.kodiak.swift.models.SwiftConstructor
import com.copperleaf.kodiak.swift.models.SwiftParameter

fun SourceKittenSubstructure.toInitializerDoc(structure: SourceKittenSubstructure): SwiftConstructor {
    val ownStructure = this.findMatch(structure)

    val methodName = this.name.split("(").first().trim()
    val modifiers = this.getModifiers()
    val parameters = ownStructure?.childrenOfType(SwiftSubstructureKind.PARAMETER) { it.toParameterDoc() } ?: emptyList()

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
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(CommentComponent(TEXT, "func"))
    list.add(CommentComponent(TYPE_NAME, " $methodName"))
    list.addAll(parameters.toParameterListSignature())

    return list
}
