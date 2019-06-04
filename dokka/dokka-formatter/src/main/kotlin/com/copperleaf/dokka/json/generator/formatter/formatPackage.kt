package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinPackageDoc
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.packageLike: Boolean get() = this.kind == NodeKind.Package

fun DocumentationNode.toPackageDoc(): KotlinPackageDoc {
    assert(this.kind == NodeKind.Package) { "node must be a Package" }

    val internalMethods = this.members
            .filter { it.isMethod }
            .map { it.toMethod() }
    val externalMethods = this.members
            .filter { it.kind == NodeKind.ExternalClass }
            .flatMap { it.members }
            .filter { it.isMethod }
            .map { it.toMethod() }

    return KotlinPackageDoc(
            this,
            this.members.filter { it.classLike }.map { it.toClassDoc(false) },
            internalMethods + externalMethods,
            this.simpleName,
            this.qualifiedName,
            this.contentText,
            this.contentTags
    )
}