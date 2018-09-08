package com.copperleaf.dokka.json.generator

import com.copperleaf.dokka.json.models.KotlinClassDoc
import com.copperleaf.dokka.json.models.KotlinConstructor
import com.copperleaf.dokka.json.models.KotlinField
import com.copperleaf.dokka.json.models.KotlinMethod
import com.copperleaf.dokka.json.models.KotlinPackageDoc
import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.dokka.json.models.KotlinReturnValue
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.FormattedOutputBuilder
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType

class DokkaJsonFormatter(val to: StringBuilder) : FormattedOutputBuilder {
    override fun appendNodes(nodes: Iterable<DocumentationNode>) {
        val node = nodes.first()

        if (node.classLike) {
            to.append(documentationNodeToClassDoc(node, true).toJson())
        }
        else if (node.kind == NodeKind.Package) {
            to.append(documentationNodeToPackageDoc(node).toJson())
        }
        else {
            // ignore, we are only documenting classes and packages for now
        }
    }

    private fun documentationNodeToPackageDoc(node: DocumentationNode): KotlinPackageDoc {
        assert(node.kind == NodeKind.Package) { "node must be a Package" }

        return KotlinPackageDoc(
                node.members.filter { it.classLike }.map { documentationNodeToClassDoc(it, false) },
                node.simpleName,
                node.qualifiedName,
                node.contentText,
                node.summary.textLength
        )
    }

    private fun documentationNodeToClassDoc(node: DocumentationNode, deep: Boolean = false): KotlinClassDoc {
        assert(node.classLike) { "node must be a Class-like" }

        val constructors: List<KotlinConstructor> = if (deep) documentationNodeToClassDocConstructors(node) else emptyList()
        val methods: List<KotlinMethod> = if (deep) documentationNodeToClassDocMethods(node) else emptyList()
        val fields: List<KotlinField> = if (deep) documentationNodeToClassDocFields(node) else emptyList()

        return KotlinClassDoc(
                node.path.map { it.name }.filterNot { it.isEmpty() }.first(),
                node.kind.toString(),
                node.simpleName,
                node.qualifiedName,
                node.contentText,
                node.summary.textLength,
                constructors,
                methods,
                fields
        )
    }

    private fun documentationNodeToClassDocConstructors(node: DocumentationNode): List<KotlinConstructor> {
        assert(node.classLike) { "node must be a Class-like" }
        return node.members.filter { it.isConstructor }.map { documentationNodeToConstructor(it) }
    }

    private fun documentationNodeToConstructor(node: DocumentationNode): KotlinConstructor {
        assert(node.isConstructor) { "node must be a Constructor" }
        return KotlinConstructor(
                node.simpleName,
                node.qualifiedName,
                node.contentText,
                node.summary.textLength,
                node.modifiers,
                node.parameters,
                node.signature
        )
    }

    private fun documentationNodeToClassDocMethods(node: DocumentationNode): List<KotlinMethod> {
        assert(node.classLike) { "node must be a Class-like" }
        return node.members.filter { it.isMethod }.map { documentationNodeToMethod(it) }
    }

    private fun documentationNodeToMethod(node: DocumentationNode): KotlinMethod {
        assert(node.isMethod) { "node must be a Function" }
        return KotlinMethod(
                node.simpleName,
                node.qualifiedName,
                node.contentText,
                node.summary.textLength,
                node.modifiers,
                node.parameters,
                node.returnValue,
                node.signature
        )
    }

    private fun documentationNodeToClassDocFields(node: DocumentationNode): List<KotlinField> {
        assert(node.classLike) { "node must be a Class-like" }
        return node.members.filter { it.isField }.map { documentationNodeToField(it) }
    }

    private fun documentationNodeToField(node: DocumentationNode): KotlinField {
        assert(node.isField) { "node must be a Field or Property" }
        return KotlinField(
                node.simpleName,
                node.qualifiedName,
                node.contentText,
                node.summary.textLength,
                node.modifiers,
                node.type,
                node.signature
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
                                it.type
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
                        it.type
                )
            }
        }

    private val DocumentationNode.signature: String
        get() {
            return this.details.first { it.kind == NodeKind.Signature }.simpleName
        }

    private val DocumentationNode.qualifiedName: String
        get() {
            if (kind == NodeKind.Type) {
                return qualifiedNameFromType()
            }
            return path.drop(1).map { it.name }.filter { it.length > 0 }.joinToString(".")
        }

    private val DocumentationNode.simpleName: String
        get() {
            if (kind == NodeKind.Type) {
                return qualifiedNameFromType()
            }
            return path.drop(1).map { it.name }.filter { it.length > 0 }.last().substringAfter('$')
        }

    private val DocumentationNode.type: String
        get() {
            if (kind == NodeKind.Type) {
                return qualifiedNameFromType()
            }
            return this.details.firstOrNull { it.kind == NodeKind.Type }?.qualifiedNameFromType() ?: ""
        }

}
