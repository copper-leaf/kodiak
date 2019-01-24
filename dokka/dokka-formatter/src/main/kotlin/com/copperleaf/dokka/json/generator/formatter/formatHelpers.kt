package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType

val DocumentationNode.modifiers: List<String>
    get() = this.details(NodeKind.Modifier)
            .map { it.name }
            .filter { !arrayOf("public", "final").contains(it) }

val DocumentationNode.simpleName: String
    get() {
        return path.drop(1).map { it.name }.filter { it.isNotEmpty() }.last().substringAfter('$')
    }

val DocumentationNode.qualifiedName: String
    get() {
        return if (kind == NodeKind.Type) {
            this.qualifiedNameFromType()
        }
        else {
            path.drop(1).map { it.name }.filter { it.isNotEmpty() }.joinToString(".")
        }
    }

fun DocumentationNode.asType(): DocumentationNode {
    return if (kind == NodeKind.Type) this else this.detailOrNull(NodeKind.Type) ?: {
        println("other node requesting type: ${this.kind} ${this.name}")
        this
    }()
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

fun List<String>.toModifierListSignature(): List<SignatureComponent> {
    return this.map { SignatureComponent("modifier", "$it ", "") }
}

fun List<DocumentationNode>.toListSignature(
        childMapper: (DocumentationNode) -> List<SignatureComponent>,
        prefix: List<SignatureComponent> = emptyList(),
        postfix: List<SignatureComponent> = emptyList(),
        separator: List<SignatureComponent> = listOf(SignatureComponent("punctuation", ", ", ""))
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    if (this.isNotEmpty()) {
        list.addAll(prefix)
        this.forEachIndexed { index, superclass ->
            list.addAll(childMapper(superclass))
            if (index < this.size - 1) {
                list.addAll(separator)
            }
        }
        list.addAll(postfix)
    }

    return list
}