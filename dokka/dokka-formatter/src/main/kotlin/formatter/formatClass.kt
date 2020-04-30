package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.kotlin.models.KotlinClass
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType
import org.jetbrains.dokka.ignoredSupertypes

val DocumentationNode.classLike: Boolean get() = NodeKind.classLike.contains(this.kind)

fun DocumentationNode.toClassDoc(deep: Boolean): KotlinClass {
    assert(this.classLike) { "node must be a Class-like" }

    val modifiers = this.modifiers
    val supertypes = this
        .details(NodeKind.Supertype)
        .map { it.qualifiedNameFromType() to it }
        .filterNot { it.first in listOf("kotlin.Annotation", "kotlin.Enum") }

    val superclass = supertypes.filter { it.second.superclassType != null }.singleOrNull()?.second
    val interfaces = supertypes.filter { it.second.superclassType == null }.toMap()

    return KotlinClass(
        this,
        this.path.map { it.name }.filterNot { it.isEmpty() }.first(),
        superclass?.name,
        interfaces.keys.toList(),
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
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    when (this.kind) {
        NodeKind.Object -> {
            list.addAll(modifiers.toModifierListSignature())
            list.add(CommentComponent("keyword", "object "))
        }
        NodeKind.Interface -> {
            list.addAll((modifiers - "abstract").toModifierListSignature())
            list.add(CommentComponent("keyword", "interface "))
        }
        else -> {
            list.addAll(modifiers.toModifierListSignature())
            list.add(CommentComponent("keyword", "class "))
        }
    }
    list.add(CommentComponent(TYPE_NAME, this.simpleName, this.qualifiedName))
    list.addAll(this.toTypeParameterDeclarationSignature())
    list.addAll(this.toSuperclassDeclarationSignature())

    return list
}

fun DocumentationNode.toSuperclassDeclarationSignature(): List<CommentComponent> {
    return this.details(NodeKind.Supertype).toListSignature(
        childMapper = { it.toTypeSignature() },
        prefix = listOf(CommentComponent("punctuation", ": "))
    )
}
