package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinMethod
import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.dokka.json.models.KotlinReturnValue
import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.isMethod: Boolean get() = this.kind == NodeKind.Function

fun DocumentationNode.toMethod(): KotlinMethod {
    assert(this.isMethod) { "node must be a Function" }
    val modifiers = this.modifiers
    val parameters = this.parameters
    val returnValue = this.returnValue
    return KotlinMethod(
            this,
            this.simpleName,
            this.qualifiedName,
            this.contentText,
            this.summary.textLength,
            modifiers,
            parameters,
            returnValue,
            this.methodSignature(
                    modifiers,
                    parameters,
                    returnValue
            )
    )
}

fun DocumentationNode.methodSignature(
        modifiers: List<String>,
        parameters: List<KotlinParameter>,
        returnValue: KotlinReturnValue
): List<SignatureComponent> {
    val signatureComponents = mutableListOf<SignatureComponent>()

    signatureComponents.appendModifierList(modifiers)
    signatureComponents.add(SignatureComponent("keyword", "fun ", ""))
    signatureComponents.add(SignatureComponent("name", this.simpleName, ""))
    signatureComponents.appendParameterList(parameters)

    if (returnValue.name != "Unit") {
        signatureComponents.add(SignatureComponent("punctuation", ": ", ""))
        val node = returnValue.node as DocumentationNode
        val nodeType = node.asType()
        signatureComponents.appendParameterType(nodeType)
    }

    return signatureComponents
}

val DocumentationNode.returnValue: KotlinReturnValue
    get() {
        val it = this.detail(NodeKind.Type)
        return KotlinReturnValue(
                it,
                it.simpleName,
                it.qualifiedName,
                it.contentText,
                it.summary.textLength,
                it.simpleType,
                it.nullable
        )
    }