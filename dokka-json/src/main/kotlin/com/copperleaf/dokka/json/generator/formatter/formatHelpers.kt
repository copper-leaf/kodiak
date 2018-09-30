package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType

val DocumentationNode.modifiers: List<String> get() = this.details(NodeKind.Modifier)
        .map { it.name }
        .filter { !arrayOf("public", "final").contains(it) }

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

fun DocumentationNode.asType(): DocumentationNode {
        return if (kind == NodeKind.Type) this else this.detail(NodeKind.Type)
    }

val DocumentationNode.simpleType: String
    get() {
        return asType().simpleName
    }

val DocumentationNode.qualifiedType: String
    get() {
        return asType().qualifiedName
    }

val DocumentationNode.nullable: Boolean
    get() {
        return asType().details(NodeKind.NullabilityModifier).singleOrNull() != null
    }

fun MutableList<SignatureComponent>.appendModifierList(modifiers: List<String>) {
    for (modifier in modifiers) {
        add(SignatureComponent("modifier", "$modifier ", ""))
    }
}
