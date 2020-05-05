package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.INHERITED
import com.copperleaf.kodiak.common.RichTextComponent.Companion.COMPOSED
import com.copperleaf.kodiak.kotlin.models.KotlinPackage
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.packageLike: Boolean get() = this.kind == NodeKind.Package

fun DocumentationNode.toPackageDoc(deep: Boolean): KotlinPackage {
    assert(this.kind == NodeKind.Package) { "node must be a Package" }

    return KotlinPackage(
        this,
        this.simpleName,
        this.qualifiedName,
        "",
        emptyList(),
        this.getComment(),
        if (deep) this.members.filter { it.classLike }.map { it.toClassDoc(false) } else emptyList(),
        emptyList(),
        if (deep) run {
            val internalMethods = this.members
                    .filter { it.isMethod }
                    .map { it.toMethod() }
            val externalMethods = this.members
                    .filter { it.kind == NodeKind.ExternalClass }
                    .flatMap { it.members }
                    .filter { it.isMethod }
                    .map { it.toMethod() }
            internalMethods + externalMethods
        } else emptyList(),
        if (deep) this.members.filter { it.isField }.map { it.toField() } else emptyList(),
        if (deep) this.members.filter { it.isTypealias }.map { it.toTypealiasDoc() } else emptyList(),
        this.packageSignature()
    )
}

fun DocumentationNode.packageSignature(): List<RichTextComponent> {
    return listOf(
        RichTextComponent(TEXT, "package "),
        RichTextComponent(TYPE_NAME, this.simpleName, this.qualifiedName)
    )
}
