package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import com.copperleaf.kodiak.swift.internal.models.SwiftSubstructureKind
import com.copperleaf.kodiak.swift.models.SwiftMethod
import com.copperleaf.kodiak.swift.models.SwiftParameter
import com.copperleaf.kodiak.swift.models.SwiftReturnType

fun SourceKittenSubstructure.toFunctionDoc(structure: SourceKittenSubstructure): SwiftMethod {
    val ownStructure = this.findMatch(structure)

    val methodName = this.name.split("(").first().trim()
    val modifiers = this.getModifiers()
    val parameters = ownStructure?.childrenOfType(SwiftSubstructureKind.PARAMETER) { it.toParameterDoc() } ?: emptyList()
    val returnType = this.toReturnType()

    return SwiftMethod(
        this,
        this.kind.name,
        methodName,
        this.name,
        modifiers,
        this.getComment(),
        parameters,
        returnType,
        this.functionSignature(
            methodName,
            modifiers,
            parameters,
            returnType
        )
    )
}

fun SourceKittenSubstructure.toReturnType(): SwiftReturnType {
    return SwiftReturnType(
        this,
        this.name,
        this.name,
        this.getModifiers(),
        this.getComment(),
        this.typenameRawValue,
        this.typename,
        this.returnValueSignature()
    )
}

fun SourceKittenSubstructure.returnValueSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(TYPE_NAME, " ${this.typenameRawValue}", this.typename))

    return list
}

fun SourceKittenSubstructure.functionSignature(
    methodName: String,
    modifiers: List<String>,
    parameters: List<SwiftParameter>,
    returnType: SwiftReturnType
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(CommentComponent(TEXT, "func"))
    list.add(CommentComponent("name", " $methodName"))
    list.addAll(parameters.toParameterListSignature())
    list.add(CommentComponent(PUNCTUATION, " -> "))
    list.addAll(returnType.signature)

    return list
}
