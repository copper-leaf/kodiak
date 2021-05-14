package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.kotlin.models.KotlinParameter
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.qualifiedNameFromType

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
): List<RichTextComponent> {
    return listOf(
        *modifiers.toModifierListSignature().toTypedArray(),
        RichTextComponent(TEXT, this.name),
        RichTextComponent(PUNCTUATION, ": "),
        *this.asType().toTypeSignature().toTypedArray()
    )
}

fun List<KotlinParameter>.toParameterListSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()
    list.add(RichTextComponent(PUNCTUATION, "("))
    this.forEachIndexed { index, parameter ->
        list.addAll(parameter.signature)

        if (index < this.size - 1) {
            list.add(RichTextComponent(PUNCTUATION, ", "))
        }
    }
    list.add(RichTextComponent(PUNCTUATION, ")"))

    return list
}

fun DocumentationNode.isFunctionalType(): Boolean {
    val typeArguments = details(NodeKind.Type)
    val functionalTypeName = "Function${typeArguments.count() - 1}"
    val suspendFunctionalTypeName = "Suspend$functionalTypeName"
    return name == functionalTypeName || name == suspendFunctionalTypeName
}

fun DocumentationNode.toTypeSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()
    if (isFunctionalType()) {
        list.addAll(this.toFunctionalTypeSignature())
    } else {
        list.addAll(this.toNonFunctionalTypeSignature())
    }

    val defaultValue = this.detailOrNull(NodeKind.Value)?.name
    if (defaultValue != null) {
        list.add(RichTextComponent(PUNCTUATION, " = "))
        list.add(RichTextComponent(TEXT, defaultValue, defaultValue))
    }

    return list
}

fun DocumentationNode.toNonFunctionalTypeSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.add(RichTextComponent(TYPE_NAME, this.simpleType, this.qualifiedNameFromType()))

    list.addAll(
        this.details(NodeKind.Type).toListSignature(
            childMapper = { it.toTypeSignature() },
            prefix = listOf(RichTextComponent(PUNCTUATION, "<")),
            postfix = listOf(RichTextComponent(PUNCTUATION, ">"))
        )
    )

    if (this.nullable) {
        list.add(RichTextComponent(PUNCTUATION, "?"))
    }

    return list
}

fun DocumentationNode.toFunctionalTypeSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    var typeArguments = this.details(NodeKind.Type)

    if (this.name.startsWith("Suspend")) {
        list.add(RichTextComponent(TEXT, "suspend "))
    }

    // function receiver
    val isExtension = this.annotations.any { it.name == "ExtensionFunctionType" }
    if (isExtension) {
        list.addAll(typeArguments.first().toTypeSignature())
        list.add(RichTextComponent(PUNCTUATION, "."))
        typeArguments = typeArguments.drop(1)
    }

    // function parameters
    list.add(RichTextComponent(PUNCTUATION, "("))
    typeArguments.dropLast(1).forEachIndexed { index, parameter ->
        list.addAll(parameter.toTypeSignature())
        if (index < typeArguments.size - 2) {
            list.add(RichTextComponent(PUNCTUATION, ", "))
        }
    }
    list.add(RichTextComponent(PUNCTUATION, ")"))

    // function return
    list.add(RichTextComponent(PUNCTUATION, "->"))
    list.addAll(typeArguments.last().toTypeSignature())

    return list
}

// Type Params
// ----------------------------------------------------------------------------------------------------------------------

fun DocumentationNode.toTypeParameterDeclarationSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    val typeArguments = this.details(NodeKind.TypeParameter)
    if (typeArguments.isNotEmpty()) {
        list.add(RichTextComponent(PUNCTUATION, "<"))
        typeArguments.forEachIndexed { index, typeParameter ->
            list.add(RichTextComponent(TEXT, typeParameter.name))

            val upperBound = typeParameter.detailOrNull(NodeKind.UpperBound)
            if (upperBound != null) {
                list.add(RichTextComponent(PUNCTUATION, " : "))
                list.addAll(upperBound.asType().toTypeSignature())
            }

            if (index < typeArguments.size - 1) {
                list.add(RichTextComponent(PUNCTUATION, ", "))
            }
        }
        list.add(RichTextComponent(PUNCTUATION, "> "))
    }

    return list
}
