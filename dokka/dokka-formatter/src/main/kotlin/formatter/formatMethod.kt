package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.kotlin.models.KotlinMethod
import com.copperleaf.kodiak.kotlin.models.KotlinParameter
import com.copperleaf.kodiak.kotlin.models.KotlinReceiver
import com.copperleaf.kodiak.kotlin.models.KotlinReturnType
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION

val DocumentationNode.isMethod: Boolean get() = this.kind in listOf(NodeKind.Function)
val DocumentationNode.isCompanionMethod: Boolean get() = this.kind in listOf(NodeKind.CompanionObjectFunction)

fun DocumentationNode.toMethod(): KotlinMethod {
    assert(this.isMethod || this.isCompanionMethod) { "node must be a Function" }
    val modifiers = this.modifiers
    val parameters = this.parameters
    val receiver = this.receiverType
    val returnType = this.returnType
    return KotlinMethod(
        this,
        this.simpleName,
        this.qualifiedName,
        modifiers,
        this.getComment(),
        receiver,
        parameters,
        returnType,
        this.methodSignature(
            modifiers,
            parameters,
            receiver,
            returnType
        )
    )
}

fun DocumentationNode.methodSignature(
    modifiers: List<String>,
    parameters: List<KotlinParameter>,
    receiverType: KotlinReceiver?,
    returnType: KotlinReturnType
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(CommentComponent(TEXT, "fun "))

    list.addAll(this.toTypeParameterDeclarationSignature())

    if (receiverType != null) {
        list.addAll(receiverType.signature)
        list.add(CommentComponent(PUNCTUATION, "."))
    }

    list.add(CommentComponent(TEXT, this.simpleName))
    list.addAll(parameters.toParameterListSignature())

    if (returnType.name != "Unit") {
        list.add(CommentComponent(PUNCTUATION, ": "))
        list.addAll(returnType.signature)
    }

    return list
}

val DocumentationNode.returnType: KotlinReturnType
    get() {
        val it = this.detail(NodeKind.Type)
        return KotlinReturnType(
            it,
            it.simpleName,
            it.qualifiedName,
            emptyList(),
            it.getComment("Return", null),
            it.simpleType,
            it.qualifiedType,
            it.asType().toTypeSignature()
        )
    }

val DocumentationNode.receiverType: KotlinReceiver?
    get() {
        val it = this.detailOrNull(NodeKind.Receiver)
        return if (it == null)
            null
        else
            KotlinReceiver(
                it,
                it.simpleName,
                it.qualifiedName,
                emptyList(),
                it.getComment("Receiver", null),
                it.simpleType,
                it.qualifiedType,
                it.asType().toTypeSignature()
            )
    }

