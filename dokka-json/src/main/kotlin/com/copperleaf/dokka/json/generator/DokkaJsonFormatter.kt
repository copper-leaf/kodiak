package com.copperleaf.dokka.json.generator

import com.copperleaf.dokka.json.models.KotlinClassDoc
import com.copperleaf.dokka.json.models.KotlinConstructor
import com.copperleaf.dokka.json.models.KotlinField
import com.copperleaf.dokka.json.models.KotlinMethod
import com.copperleaf.dokka.json.models.KotlinPackageDoc
import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.dokka.json.models.KotlinReturnValue
import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.FormattedOutputBuilder
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType

class DokkaJsonFormatter(val to: StringBuilder) : FormattedOutputBuilder {
    override fun appendNodes(nodes: Iterable<DocumentationNode>) {
        val node = nodes.first()

        if (node.classLike) {
            to.append(node.toClassDoc(true).toJson())
        }
        else if (node.kind == NodeKind.Package) {
            to.append(node.toPackageDoc().toJson())
        }
        else {
            // ignore, we are only documenting classes and packages for now
        }
    }

    private fun DocumentationNode.toPackageDoc(): KotlinPackageDoc {
        assert(this.kind == NodeKind.Package) { "node must be a Package" }

        val internalMethods = this.members
                .filter { it.isMethod }
                .map { it.toMethod() }
        val externalMethods = this.members
                .filter { it.kind == NodeKind.ExternalClass }
                .flatMap { it.members }
                .filter { it.isMethod }
                .map { it.toMethod() }

        return KotlinPackageDoc(
                this.members.filter { it.classLike }.map { it.toClassDoc(false) },
                internalMethods + externalMethods,
                this.simpleName,
                this.qualifiedName,
                this.contentText,
                this.summary.textLength
        )
    }

    private fun DocumentationNode.toClassDoc(deep: Boolean = false): KotlinClassDoc {
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

    private fun DocumentationNode.toConstructor(): KotlinConstructor {
        assert(this.isConstructor) { "node must be a Constructor" }
        val modifiers = this.modifiers
        val parameters = this.parameters
        return KotlinConstructor(
                this.simpleName,
                this.qualifiedName,
                this.contentText,
                this.summary.textLength,
                modifiers,
                parameters,
                this.constructorSignature(
                        modifiers,
                        parameters
                )
        )
    }

    private fun DocumentationNode.toMethod(): KotlinMethod {
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

    private fun DocumentationNode.toField(): KotlinField {
        assert(this.isField) { "node must be a Field or Property" }
        val modifiers = this.modifiers
        return KotlinField(
                this.simpleName,
                this.qualifiedName,
                this.contentText,
                this.summary.textLength,
                modifiers,
                this.simpleType,
                this.qualifiedType,
                this.fieldSignature(modifiers)
        )
    }

// Helpers
//----------------------------------------------------------------------------------------------------------------------

    private val DocumentationNode.contentText: String get() = DokkaContentFormatter(this).extractContent()
    private val DocumentationNode.classLike: Boolean get() = NodeKind.classLike.contains(this.kind)
    private val DocumentationNode.isConstructor: Boolean get() = this.kind == NodeKind.Constructor
    private val DocumentationNode.isMethod: Boolean get() = this.kind == NodeKind.Function
    private val DocumentationNode.isField: Boolean get() = this.kind == NodeKind.Field || this.kind == NodeKind.Property

    private val DocumentationNode.modifiers: List<String> get() = this.details.filter { it.kind == NodeKind.Modifier }.map { it.name }

    private val DocumentationNode.parameters: List<KotlinParameter>
        get() {
            return this.details
                    .filter { it.kind == NodeKind.Parameter }
                    .map {
                        KotlinParameter(
                                it.simpleName,
                                it.qualifiedName,
                                it.contentText,
                                it.summary.textLength,
                                it.simpleType,
                                it.qualifiedType
                        )
                    }
        }

    private val DocumentationNode.returnValue: KotlinReturnValue
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

    private fun DocumentationNode.constructorSignature(
            modifiers: List<String>,
            parameters: List<KotlinParameter>
    ): List<SignatureComponent> {
        val signatureComponents = mutableListOf<SignatureComponent>()

        for (modifier in modifiers) {
            signatureComponents.add(SignatureComponent("modifier", "$modifier ", ""))
        }
        signatureComponents.add(SignatureComponent("keyword", "constructor", ""))

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

        return signatureComponents
    }

    private fun DocumentationNode.methodSignature(
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

    private fun DocumentationNode.fieldSignature(
            modifiers: List<String>
    ): List<SignatureComponent> {
        val signatureComponents = mutableListOf<SignatureComponent>()

        for (modifier in modifiers) {
            signatureComponents.add(SignatureComponent("modifier", "$modifier ", ""))
        }
        signatureComponents.add(SignatureComponent("name", this.simpleName, ""))
        signatureComponents.add(SignatureComponent("punctuation", ": ", ""))
        signatureComponents.add(SignatureComponent("type", this.simpleType, this.qualifiedType))

        return signatureComponents
    }

    private val DocumentationNode.simpleName: String
        get() {
            return path.drop(1).map { it.name }.filter { it.isNotEmpty() }.last().substringAfter('$')
        }

    private val DocumentationNode.qualifiedName: String
        get() {
            return if(kind == NodeKind.Type) {
                this.qualifiedNameFromType()
            }
            else {
                path.drop(1).map { it.name }.filter { it.isNotEmpty() }.joinToString(".")
            }
        }

    private val DocumentationNode.simpleType: String
        get() {
            return (
                    if (kind == NodeKind.Type)
                        this
                    else
                        this.details.firstOrNull { it.kind == NodeKind.Type }
                    )
                    ?.simpleName ?: ""
        }

    private val DocumentationNode.qualifiedType: String
        get() {
            return (
                    if (kind == NodeKind.Type)
                        this
                    else
                        this.details.firstOrNull { it.kind == NodeKind.Type }
                    )
                    ?.qualifiedName ?: ""
        }

}
