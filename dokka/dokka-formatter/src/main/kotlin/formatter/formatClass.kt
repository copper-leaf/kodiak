package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.kotlin.models.KotlinClass
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path

val DocumentationNode.classLike: Boolean get() = NodeKind.classLike.contains(this.kind)

fun DocumentationNode.toClassDoc(deep: Boolean = false): KotlinClass {
    assert(this.classLike) { "node must be a Class-like" }

    val modifiers = this.modifiers

    return KotlinClass(
        this,
        this.path.map { it.name }.filterNot { it.isEmpty() }.first(),
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

    when(this.kind) {
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
    list.add(CommentComponent("typeName", this.simpleName, this.qualifiedName))
//    list.addAll(this.toTypeParameterDeclarationSignature())
//    list.addAll(this.toSuperclassDeclarationSignature())

    return list
}

fun DocumentationNode.toSuperclassDeclarationSignature(): List<CommentComponent> {
    return this.details(NodeKind.Supertype).toListSignature(
        childMapper = { it.toTypeSignature() },
        prefix = listOf(CommentComponent("punctuation", ": "))
    )
}