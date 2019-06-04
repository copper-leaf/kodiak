package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinConstructor
import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.json.common.CommentComponent
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
        this.contentText,
        this.contentTags,
        modifiers,
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
    list.add(CommentComponent("keyword", "constructor"))
    list.addAll(parameters.toParameterListSignature())

    return list
}