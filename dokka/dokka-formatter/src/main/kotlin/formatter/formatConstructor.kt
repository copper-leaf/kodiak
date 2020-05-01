package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.kotlin.models.KotlinConstructor
import com.copperleaf.kodiak.kotlin.models.KotlinParameter
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.CommentComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.isConstructor: Boolean get() = this.kind == NodeKind.Constructor

fun DocumentationNode.toConstructor(): KotlinConstructor {
    assert(this.isConstructor) { "node must be a Constructor" }
    val modifiers = this.modifiers
    val parameters = this.parameters
    return KotlinConstructor(
        this,
        this.simpleName,
        this.qualifiedName,
        modifiers,
        this.getComment(),
        parameters,
        this.constructorSignature(
            modifiers,
            parameters
        )
    )
}

fun DocumentationNode.constructorSignature(
    modifiers: List<String>,
    parameters: List<KotlinParameter>
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(CommentComponent(TEXT, "constructor"))
    list.addAll(parameters.toParameterListSignature())

    return list
}
