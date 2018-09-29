package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinClassDoc
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path

val DocumentationNode.classLike: Boolean get() = NodeKind.classLike.contains(this.kind)

fun DocumentationNode.toClassDoc(deep: Boolean = false): KotlinClassDoc {
    assert(this.classLike) { "node must be a Class-like" }

    return KotlinClassDoc(
            this.path.map { it.name }.filterNot { it.isEmpty() }.first(),
            this.kind.toString(),
            this.simpleName,
            this.qualifiedName,
            this.contentText,
            this.summary.textLength,
            if (deep) this.members.filter { it.isConstructor }.map { it.toConstructor() } else emptyList(),
            if (deep) this.members.filter { it.isMethod }.map { it.toMethod() } else emptyList(),
            if (deep) this.members.filter { it.isField }.map { it.toField() } else emptyList(),
            if (deep) this.extensions.filter { it.isMethod }.map { it.toMethod() } else emptyList()
    )
}