package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinClassDoc
import com.copperleaf.json.common.CommentComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path

val DocumentationNode.classLike: Boolean get() = NodeKind.classLike.contains(this.kind)

fun DocumentationNode.toClassDoc(deep: Boolean = false): KotlinClassDoc {
    assert(this.classLike) { "node must be a Class-like" }

    val modifiers = this.modifiers

    return KotlinClassDoc(
        this,
        this.path.map { it.name }.filterNot { it.isEmpty() }.first(),
        this.kind.toString(),
        this.simpleName,
        this.qualifiedName,
        this.contentText,
        this.contentTags,
        modifiers,
        if (deep) this.members.filter { it.isConstructor }.map { it.toConstructor() } else emptyList(),
        if (deep) this.members.filter { it.isMethod }.map { it.toMethod() } else emptyList(),
        if (deep) this.members.filter { it.isField }.map { it.toField() } else emptyList(),
        if (deep) this.extensions.filter { it.isMethod }.map { it.toMethod() } else emptyList(),
        this.classSignature(
            modifiers
        )
    )
}

fun DocumentationNode.classSignature(
    modifiers: List<String>
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(CommentComponent("keyword", "class "))
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