package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.kotlin.models.KotlinPackage
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.packageLike: Boolean get() = this.kind == NodeKind.Package

fun DocumentationNode.toPackageDoc(): KotlinPackage {
    assert(this.kind == NodeKind.Package) { "node must be a Package" }

    val internalMethods = this.members
        .filter { it.isMethod }
        .map { it.toMethod() }
    val externalMethods = this.members
        .filter { it.kind == NodeKind.ExternalClass }
        .flatMap { it.members }
        .filter { it.isMethod }
        .map { it.toMethod() }

    return KotlinPackage(
        this,
        this.simpleName,
        this.qualifiedName,
        emptyList(),
        this.getComment(),
        this.members.filter { it.classLike }.map { it.toClassDoc(false) },
        internalMethods + externalMethods,
        this.members.filter { it.isField }.map { it.toField() },
        this.members.filter { it.isTypealias }.map { it.toTypealiasDoc() },
        this.packageSignature()
    )
}

fun DocumentationNode.packageSignature(): List<CommentComponent> {
    return listOf(
        CommentComponent("keyword", "package "),
        CommentComponent(TYPE_NAME, this.simpleName, this.qualifiedName)
    )
}
