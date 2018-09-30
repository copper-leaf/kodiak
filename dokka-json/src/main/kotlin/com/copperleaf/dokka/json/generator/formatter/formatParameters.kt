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
                            it.contentText,
                            it.summary.textLength,
                            it.simpleType,
                            it.qualifiedType,
                            it.nullable,
                            it.detailOrNull(NodeKind.Value)?.name
                    )
                }
    }

fun MutableList<SignatureComponent>.appendParameterList(params: List<KotlinParameter>) {
    add(SignatureComponent("punctuation", "(", ""))
    params.forEachIndexed { index, parameter ->
        val node = parameter.node as DocumentationNode
        val nodeType = node.asType()

        add(SignatureComponent("name", parameter.name, ""))
        add(SignatureComponent("punctuation", ": ", ""))

        appendParameterType(nodeType)

        if (parameter.defaultValue != null) {
            add(SignatureComponent("punctuation", " = ", ""))
            add(SignatureComponent("value", parameter.defaultValue!!, parameter.defaultValue!!))
        }

        if (index < params.size - 1) {
            add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    add(SignatureComponent("punctuation", ")", ""))
}

fun DocumentationNode.isFunctionalType(): Boolean {
    val typeArguments = details(NodeKind.Type)
    val functionalTypeName = "Function${typeArguments.count() - 1}"
    val suspendFunctionalTypeName = "Suspend$functionalTypeName"
    return name == functionalTypeName || name == suspendFunctionalTypeName
}

fun MutableList<SignatureComponent>.appendParameterType(node: DocumentationNode) {
    if (node.isFunctionalType()) {
        appendFunctionalType(node)
    }
    else {
        appendNonFunctionalType(node)
    }
}

fun MutableList<SignatureComponent>.appendNonFunctionalType(node: DocumentationNode) {
    add(SignatureComponent("type", node.simpleType, node.qualifiedType))

    val typeArguments = node.details(NodeKind.Type)
    if (typeArguments.isNotEmpty()) {
        add(SignatureComponent("punctuation", "<", ""))
        typeArguments.forEachIndexed { index, parameter ->
            appendParameterType(parameter)
            if (index < typeArguments.size - 1) {
                add(SignatureComponent("punctuation", ", ", ""))
            }
        }
        add(SignatureComponent("punctuation", ">", ""))
    }

    if (node.nullable) {
        add(SignatureComponent("punctuation", "?", ""))
    }
}

fun MutableList<SignatureComponent>.appendFunctionalType(node: DocumentationNode) {
    var typeArguments = node.details(NodeKind.Type)

    if (node.name.startsWith("Suspend")) {
        add(SignatureComponent("keyword", "suspend ", ""))
    }

    // function receiver
    val isExtension = node.annotations.any { it.name == "ExtensionFunctionType" }
    if (isExtension) {
        appendParameterType(typeArguments.first())
        add(SignatureComponent("punctuation", ".", ""))
        typeArguments = typeArguments.drop(1)
    }

    // function parameters
    add(SignatureComponent("punctuation", "(", ""))
    typeArguments.dropLast(1).forEachIndexed { index, parameter ->
        appendParameterType(parameter)
        if (index < typeArguments.size - 2) {
            add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    add(SignatureComponent("punctuation", ")", ""))

    // function return
    add(SignatureComponent("punctuation", "->", ""))
    appendParameterType(typeArguments.last())
}
