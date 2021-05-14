package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.kotlin.models.KotlinConstructor
import com.copperleaf.kodiak.kotlin.models.KotlinParameter
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
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
): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(RichTextComponent(TEXT, "constructor"))
    list.addAll(parameters.toParameterListSignature())

    return list
}
