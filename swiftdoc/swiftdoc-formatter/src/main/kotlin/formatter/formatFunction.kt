package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.DocComment
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
    val typeName = this.typenameRawValue.takeIf { it.isNotBlank() } ?: "Void"
    val typeId = this.typename.takeIf { it.isNotBlank() } ?: "Void"
    return SwiftReturnType(
        this,
        this.name,
        this.name,
        emptyList(),
        DocComment(
            emptyList(),
            emptyMap()
        ),
        typeName,
        typeId,
        this.returnValueSignature(typeName, typeId)
    )
}

fun SourceKittenSubstructure.returnValueSignature(typeName: String, typeId: String): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(TYPE_NAME, " $typeName", typeId))

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
    list.add(CommentComponent(TEXT, " $methodName"))
    list.addAll(parameters.toParameterListSignature())
    if(returnType.typeId != "Void") {
        list.add(CommentComponent(PUNCTUATION, " -> "))
        list.addAll(returnType.signature)
    }

    return list
}
