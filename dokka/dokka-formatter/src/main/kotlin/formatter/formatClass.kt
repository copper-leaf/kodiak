package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.INHERITED
import com.copperleaf.kodiak.common.RichTextComponent.Companion.COMPOSED
import com.copperleaf.kodiak.kotlin.models.KotlinClass
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType

val DocumentationNode.classLike: Boolean get() = NodeKind.classLike.contains(this.kind)

fun DocumentationNode.toClassDoc(deep: Boolean): KotlinClass {
    assert(this.classLike) { "node must be a Class-like" }

    val modifiers = this.modifiers
    val supertypes = this
        .details(NodeKind.Supertype)
        .map { it.qualifiedNameFromType() to it }
        .filterNot { it.first in listOf("kotlin.Annotation", "kotlin.Enum") }

    return KotlinClass(
        this,
        this.path.map { it.name }.filterNot { it.isEmpty() }.first(),
        supertypes
            .filter { it.second.superclassType != null }
            .singleOrNull()
            ?.second
            ?.name
            ?.let { RichTextComponent(INHERITED, it, it) },
        supertypes
            .filter { it.second.superclassType == null }
            .toMap()
            .keys
            .toList()
            ?.map { RichTextComponent(COMPOSED, it, it) },
        this.kind.toString(),
        this.simpleName,
        this.qualifiedName,
        modifiers,
        this.getComment(),
        if (deep) this.members.filter { it.isConstructor }.map { it.toConstructor() } else emptyList(),
        if (deep) this.members.filter { it.isMethod }.map { it.toMethod() } else emptyList(),
        if (deep) this.members.filter { it.isField }.map { it.toField() } else emptyList(),
        if (deep) this.extensions.filter { it.isMethod }.map { it.toMethod() } else emptyList(),
        this.classSignature(
            modifiers
        ),
        if (deep && this.hasCompanionObject) this.toCompanionObjectDoc() else null,
        if (deep) this.members.filter { it.isEnumItem }.map { it.toEnumConstantDoc() } else emptyList()
    )
}

fun DocumentationNode.classSignature(
    modifiers: List<String>
): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    when (this.kind) {
        NodeKind.Object -> {
            list.addAll(modifiers.toModifierListSignature())
            list.add(RichTextComponent(TEXT, "object "))
        }
        NodeKind.Interface -> {
            list.addAll((modifiers - "abstract").toModifierListSignature())
            list.add(RichTextComponent(TEXT, "interface "))
        }
        else -> {
            list.addAll(modifiers.toModifierListSignature())
            list.add(RichTextComponent(TEXT, "class "))
        }
    }
    list.add(RichTextComponent(TYPE_NAME, this.simpleName, this.qualifiedName))
    list.addAll(this.toTypeParameterDeclarationSignature())
    list.addAll(this.toSuperclassDeclarationSignature())

    return list
}

fun DocumentationNode.toSuperclassDeclarationSignature(): List<RichTextComponent> {
    return this.details(NodeKind.Supertype).toListSignature(
        childMapper = { it.toTypeSignature() },
        prefix = listOf(RichTextComponent(PUNCTUATION, ": "))
    )
}
