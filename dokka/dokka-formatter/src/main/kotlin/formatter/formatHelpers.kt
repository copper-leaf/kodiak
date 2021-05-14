package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import clog.Clog

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
        } else {
            path.drop(1).map { it.name }.filter { it.isNotEmpty() }.joinToString(".")
        }
    }

fun DocumentationNode.asType(): DocumentationNode {
    return if (kind in listOf(NodeKind.Type, NodeKind.UpperBound, NodeKind.Supertype)) {
        this
    } else {
        this.detailOrNull(NodeKind.Type) ?: {
            Clog.i("other node requesting typeName: ${this.kind} ${this.name}")
            this
        }()
    }
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

fun List<String>.toModifierListSignature(): List<RichTextComponent> {
    return this.map { RichTextComponent(TEXT, "$it ") }
}

fun List<DocumentationNode>.toListSignature(
    childMapper: (DocumentationNode) -> List<RichTextComponent>,
    prefix: List<RichTextComponent> = emptyList(),
    postfix: List<RichTextComponent> = emptyList(),
    separator: List<RichTextComponent> = listOf(RichTextComponent(PUNCTUATION, ", "))
): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

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
