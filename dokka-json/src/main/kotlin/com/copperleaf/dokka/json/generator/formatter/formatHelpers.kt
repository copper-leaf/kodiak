package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType

val DocumentationNode.modifiers: List<String> get() = this.details(NodeKind.Modifier)
        .map { it.name }
        .filter { !arrayOf("public", "final").contains(it) }

val DocumentationNode.parameters: List<KotlinParameter>
    get() {
        return this.details(NodeKind.Parameter)
                .map {
                    KotlinParameter(
                            it.simpleName,
                            it.qualifiedName,
                            it.contentText,
                            it.summary.textLength,
                            it.simpleType,
                            it.qualifiedType,
                            it.detailOrNull(NodeKind.Type)?.details(NodeKind.NullabilityModifier)?.singleOrNull() != null,
                            it.detailOrNull(NodeKind.Value)?.name
                    )
                }
    }

val DocumentationNode.simpleName: String
    get() {
        return path.drop(1).map { it.name }.filter { it.isNotEmpty() }.last().substringAfter('$')
    }

val DocumentationNode.qualifiedName: String
    get() {
        return if(kind == NodeKind.Type) {
            this.qualifiedNameFromType()
        }
        else {
            path.drop(1).map { it.name }.filter { it.isNotEmpty() }.joinToString(".")
        }
    }

val DocumentationNode.simpleType: String
    get() {
        return (if (kind == NodeKind.Type) this else this.detail(NodeKind.Type)).simpleName
    }

val DocumentationNode.qualifiedType: String
    get() {
        return (if (kind == NodeKind.Type) this else this.detail(NodeKind.Type)).qualifiedName
    }

fun MutableList<SignatureComponent>.appendModifierList(modifiers: List<String>) {
    for (modifier in modifiers) {
        add(SignatureComponent("modifier", "$modifier ", ""))
    }
}

fun MutableList<SignatureComponent>.appendParameterList(params: List<KotlinParameter>) {
    add(SignatureComponent("punctuation", "(", ""))
    params.forEachIndexed { index, parameter ->
        add(SignatureComponent("name", parameter.name, ""))
        add(SignatureComponent("punctuation", ": ", ""))
        add(SignatureComponent("type", parameter.type, parameter.qualifiedType))

        if(parameter.nullable) {
            add(SignatureComponent("punctuation", "?", ""))
        }

        if(parameter.defaultValue != null) {
            add(SignatureComponent("punctuation", " = ", ""))
            add(SignatureComponent("value", parameter.defaultValue!!, parameter.defaultValue!!))
        }

        if (index < params.size - 1) {
            add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    add(SignatureComponent("punctuation", ")", ""))
}