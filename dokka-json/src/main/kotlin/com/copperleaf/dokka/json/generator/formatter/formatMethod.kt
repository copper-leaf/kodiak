package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinMethod
import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.dokka.json.models.KotlinReceiverType
import com.copperleaf.dokka.json.models.KotlinReturnType
import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.isMethod: Boolean get() = this.kind == NodeKind.Function

fun DocumentationNode.toMethod(): KotlinMethod {
    assert(this.isMethod) { "node must be a Function" }
    val modifiers = this.modifiers
    val parameters = this.parameters
    val receiverType = this.receiverType
    val returnType = this.returnType
    return KotlinMethod(
            this,
            this.simpleName,
            this.qualifiedName,
            this.contentText,
            this.summary.textLength,
            modifiers,
            parameters,
            receiverType,
            returnType,
            this.methodSignature(
                    modifiers,
                    parameters,
                    receiverType,
                    returnType
            )
    )
}

fun DocumentationNode.methodSignature(
        modifiers: List<String>,
        parameters: List<KotlinParameter>,
        receiverType: KotlinReceiverType?,
        returnType: KotlinReturnType
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(SignatureComponent("keyword", "fun ", ""))

    list.addAll(this.toTypeParameterDeclarationSignature())

    if (receiverType != null) {
        list.addAll(receiverType.signature)
        list.add(SignatureComponent("punctuation", ".", ""))
    }

    list.add(SignatureComponent("name", this.simpleName, ""))
    list.addAll(parameters.toParameterListSignature())

    if (returnType.name != "Unit") {
        list.add(SignatureComponent("punctuation", ": ", ""))
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
                it.contentText("Return", null),
                it.summary.textLength,
                it.simpleType,
                it.qualifiedType,
                it.asType().toTypeSignature()
        )
    }

val DocumentationNode.receiverType: KotlinReceiverType?
    get() {
        val it = this.detailOrNull(NodeKind.Receiver)
        return if (it == null)
            null
        else
            KotlinReceiverType(
                    it,
                    it.simpleName,
                    it.qualifiedName,
                    it.contentText("Receiver", null),
                    it.summary.textLength,
                    it.simpleType,
                    it.qualifiedType,
                    it.asType().toTypeSignature()
            )
    }

