package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.dokka.json.models.SignatureComponent
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
                            it.contentText("Parameters", it.simpleName),
                            it.summary.textLength,
                            it.simpleType,
                            it.qualifiedType,
                            it.detailOrNull(NodeKind.Value)?.name,
                            it.asType().toTypeSignature()
                    )
                }
    }

fun List<KotlinParameter>.toParameterListSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()
    list.add(SignatureComponent("punctuation", "(", ""))
    this.forEachIndexed { index, parameter ->
        list.add(SignatureComponent("name", parameter.name, ""))
        list.add(SignatureComponent("punctuation", ": ", ""))

        list.addAll(parameter.signature)

        if (index < this.size - 1) {
            list.add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    list.add(SignatureComponent("punctuation", ")", ""))

    return list
}

fun DocumentationNode.isFunctionalType(): Boolean {
    val typeArguments = details(NodeKind.Type)
    val functionalTypeName = "Function${typeArguments.count() - 1}"
    val suspendFunctionalTypeName = "Suspend$functionalTypeName"
    return name == functionalTypeName || name == suspendFunctionalTypeName
}

fun DocumentationNode.toTypeSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()
    if (isFunctionalType()) {
        list.addAll(this.toFunctionalTypeSignature())
    }
    else {
        list.addAll(this.toNonFunctionalTypeSignature())
    }

    val defaultValue = this.detailOrNull(NodeKind.Value)?.name
    if (defaultValue != null) {
        list.add(SignatureComponent("punctuation", " = ", ""))
        list.add(SignatureComponent("value", defaultValue, defaultValue))
    }

    return list
}

fun DocumentationNode.toNonFunctionalTypeSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.add(SignatureComponent("type", this.simpleType, this.qualifiedType))

    this.details(NodeKind.Type).toListSignature(
            childMapper = { it.toTypeSignature() },
            prefix = listOf(SignatureComponent("punctuation", "<", "")),
            postfix = listOf(SignatureComponent("punctuation", ">", ""))
    )

    if (this.nullable) {
        list.add(SignatureComponent("punctuation", "?", ""))
    }

    return list
}

fun DocumentationNode.toFunctionalTypeSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    var typeArguments = this.details(NodeKind.Type)

    if (this.name.startsWith("Suspend")) {
        list.add(SignatureComponent("keyword", "suspend ", ""))
    }

    // function receiver
    val isExtension = this.annotations.any { it.name == "ExtensionFunctionType" }
    if (isExtension) {
        list.addAll(typeArguments.first().toTypeSignature())
        list.add(SignatureComponent("punctuation", ".", ""))
        typeArguments = typeArguments.drop(1)
    }

    // function parameters
    list.add(SignatureComponent("punctuation", "(", ""))
    typeArguments.dropLast(1).forEachIndexed { index, parameter ->
        list.addAll(parameter.toTypeSignature())
        if (index < typeArguments.size - 2) {
            list.add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    list.add(SignatureComponent("punctuation", ")", ""))

    // function return
    list.add(SignatureComponent("punctuation", "->", ""))
    list.addAll(typeArguments.last().toTypeSignature())

    return list
}


// Type Params
//----------------------------------------------------------------------------------------------------------------------

fun DocumentationNode.toTypeParameterDeclarationSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    val typeArguments = this.details(NodeKind.TypeParameter)
    if (typeArguments.isNotEmpty()) {
        list.add(SignatureComponent("punctuation", "<", ""))
        typeArguments.forEachIndexed { index, typeParameter ->
            list.add(SignatureComponent("name", typeParameter.name, ""))

            val upperBound = typeParameter.detailOrNull(NodeKind.UpperBound)
            if (upperBound != null) {
                list.add(SignatureComponent("punctuation", " : ", ""))
                list.addAll(upperBound.asType().toTypeSignature())
            }

            if (index < typeArguments.size - 1) {
                list.add(SignatureComponent("punctuation", ", ", ""))
            }
        }
        list.add(SignatureComponent("punctuation", "> ", ""))
    }

    return list
}
