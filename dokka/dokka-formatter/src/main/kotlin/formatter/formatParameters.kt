package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.kotlin.models.KotlinParameter
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.parameters: List<KotlinParameter>
    get() {
        return this.details(NodeKind.Parameter)
            .map {
                KotlinParameter(
                    it,
                    it.simpleName,
                    it.qualifiedName,
                    it.modifiers,
                    it.getComment("Parameters", it.simpleName),
                    it.simpleType,
                    it.qualifiedType,
                    it.parameterSignature(it.modifiers),
                    it.detailOrNull(NodeKind.Value)?.name
                )
            }
    }

fun DocumentationNode.parameterSignature(
    modifiers: List<String>
): List<CommentComponent> {
    return listOf(
        *modifiers.toModifierListSignature().toTypedArray(),
        CommentComponent("name", this.name),
        CommentComponent("punctuation", ": "),
        *this.asType().toTypeSignature().toTypedArray()
    )
}

fun List<KotlinParameter>.toParameterListSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()
    list.add(CommentComponent("punctuation", "("))
    this.forEachIndexed { index, parameter ->
        list.addAll(parameter.signature)

        if (index < this.size - 1) {
            list.add(CommentComponent("punctuation", ", "))
        }
    }
    list.add(CommentComponent("punctuation", ")"))

    return list
}

fun DocumentationNode.isFunctionalType(): Boolean {
    val typeArguments = details(NodeKind.Type)
    val functionalTypeName = "Function${typeArguments.count() - 1}"
    val suspendFunctionalTypeName = "Suspend$functionalTypeName"
    return name == functionalTypeName || name == suspendFunctionalTypeName
}

fun DocumentationNode.toTypeSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()
    if (isFunctionalType()) {
        list.addAll(this.toFunctionalTypeSignature())
    } else {
        list.addAll(this.toNonFunctionalTypeSignature())
    }

    val defaultValue = this.detailOrNull(NodeKind.Value)?.name
    if (defaultValue != null) {
        list.add(CommentComponent("punctuation", " = "))
        list.add(CommentComponent("value", defaultValue, defaultValue))
    }

    return list
}

fun DocumentationNode.toNonFunctionalTypeSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(TYPE_NAME, this.simpleType, this.qualifiedType))

    list.addAll(
        this.details(NodeKind.Type).toListSignature(
            childMapper = { it.toTypeSignature() },
            prefix = listOf(CommentComponent("punctuation", "<")),
            postfix = listOf(CommentComponent("punctuation", ">"))
        )
    )

    if (this.nullable) {
        list.add(CommentComponent("punctuation", "?"))
    }

    return list
}

fun DocumentationNode.toFunctionalTypeSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    var typeArguments = this.details(NodeKind.Type)

    if (this.name.startsWith("Suspend")) {
        list.add(CommentComponent("keyword", "suspend "))
    }

    // function receiver
    val isExtension = this.annotations.any { it.name == "ExtensionFunctionType" }
    if (isExtension) {
        list.addAll(typeArguments.first().toTypeSignature())
        list.add(CommentComponent("punctuation", "."))
        typeArguments = typeArguments.drop(1)
    }

    // function parameters
    list.add(CommentComponent("punctuation", "("))
    typeArguments.dropLast(1).forEachIndexed { index, parameter ->
        list.addAll(parameter.toTypeSignature())
        if (index < typeArguments.size - 2) {
            list.add(CommentComponent("punctuation", ", "))
        }
    }
    list.add(CommentComponent("punctuation", ")"))

    // function return
    list.add(CommentComponent("punctuation", "->"))
    list.addAll(typeArguments.last().toTypeSignature())

    return list
}


// Type Params
//----------------------------------------------------------------------------------------------------------------------

fun DocumentationNode.toTypeParameterDeclarationSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    val typeArguments = this.details(NodeKind.TypeParameter)
    if (typeArguments.isNotEmpty()) {
        list.add(CommentComponent("punctuation", "<"))
        typeArguments.forEachIndexed { index, typeParameter ->
            list.add(CommentComponent("name", typeParameter.name))

            val upperBound = typeParameter.detailOrNull(NodeKind.UpperBound)
            if (upperBound != null) {
                list.add(CommentComponent("punctuation", " : "))
                list.addAll(upperBound.asType().toTypeSignature())
            }

            if (index < typeArguments.size - 1) {
                list.add(CommentComponent("punctuation", ", "))
            }
        }
        list.add(CommentComponent("punctuation", "> "))
    }

    return list
}
