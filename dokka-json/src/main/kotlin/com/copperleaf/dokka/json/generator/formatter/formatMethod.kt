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
    val signatureComponents = mutableListOf<SignatureComponent>()

    signatureComponents.addAll(modifiers.toModifierListSignature())
    signatureComponents.add(SignatureComponent("keyword", "fun ", ""))

    if (receiverType != null) {
        signatureComponents.addAll(receiverType.signature)
        signatureComponents.add(SignatureComponent("punctuation", ".", ""))
    }

    signatureComponents.add(SignatureComponent("name", this.simpleName, ""))
    signatureComponents.addAll(parameters.toParameterListSignature())

    if (returnType.name != "Unit") {
        signatureComponents.add(SignatureComponent("punctuation", ": ", ""))
        signatureComponents.addAll(returnType.signature)
    }

    return signatureComponents
}

val DocumentationNode.returnType: KotlinReturnType
    get() {
        val it = this.detail(NodeKind.Type)
        return KotlinReturnType(
                it,
                it.simpleName,
                it.qualifiedName,
                it.contentText,
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
                    it.contentText,
                    it.summary.textLength,
                    it.simpleType,
                    it.qualifiedType,
                    it.asType().toTypeSignature()
            )
    }

