package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.kotlin.models.KotlinClass
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.path

val DocumentationNode.hasCompanionObject: Boolean get() = this.members.any { it.isCompanionMethod || it.isCompanionField }

fun DocumentationNode.toCompanionObjectDoc(): KotlinClass {
    assert(this.classLike) { "node must be a Class-like" }

    val modifiers = this.modifiers

    return KotlinClass(
        this,
        this.path.map { it.name }.filterNot { it.isEmpty() }.first(),
        this.kind.toString(),
        this.simpleName,
        "${this.qualifiedName}.companion",
        modifiers,
        this.getComment(),
        emptyList(),
        this.members.filter { it.isCompanionMethod }.map { it.toMethod() },
        this.members.filter { it.isCompanionField }.map { it.toField() },
        this.extensions.filter { it.isCompanionMethod }.map { it.toMethod() },
        this.classSignature(
            modifiers
        ),
        null,
        emptyList()
    )
}
