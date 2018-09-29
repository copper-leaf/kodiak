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

    for (modifier in modifiers) {
        signatureComponents.add(SignatureComponent("modifier", "$modifier ", ""))
    }
    signatureComponents.add(SignatureComponent("keyword", "fun ", ""))
    signatureComponents.add(SignatureComponent("name", this.simpleName, ""))

    signatureComponents.add(SignatureComponent("punctuation", "(", ""))
    parameters.forEachIndexed { index, parameter ->
        signatureComponents.add(SignatureComponent("name", parameter.name, ""))
        signatureComponents.add(SignatureComponent("punctuation", ": ", ""))
        signatureComponents.add(SignatureComponent("type", parameter.type, parameter.qualifiedType))

        if (index < parameters.size - 1) {
            signatureComponents.add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    signatureComponents.add(SignatureComponent("punctuation", ")", ""))

    if (returnValue.name != "Unit") {
        signatureComponents.add(SignatureComponent("punctuation", ": ", ""))
        signatureComponents.add(SignatureComponent("type", returnValue.name, returnValue.qualifiedName))
    }

    return signatureComponents
}

val DocumentationNode.returnValue: KotlinReturnValue
    get() {
        val it = this.details.find { it.kind == NodeKind.Type }
        if (it == null) {
            throw IllegalArgumentException("node does not have a return value")
        }
        else {
            return KotlinReturnValue(
                    it.simpleName,
                    it.qualifiedName,
                    it.contentText,
                    it.summary.textLength,
                    it.simpleType
            )
        }
    }