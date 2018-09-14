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

        return KotlinPackageDoc(
                this.members.filter { it.classLike }.map { it.toClassDoc(false) },
                this.members.filter { it.isMethod }.map { it.toMethod() },
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
        return KotlinConstructor(
                this.simpleName,
                this.qualifiedName,
                this.contentText,
                this.summary.textLength,
                this.modifiers,
                this.parameters,
                this.signature
        )
    }

    private fun DocumentationNode.toMethod(): KotlinMethod {
        assert(this.isMethod) { "node must be a Function" }
        return KotlinMethod(
                this.simpleName,
                this.qualifiedName,
                this.contentText,
                this.summary.textLength,
                this.modifiers,
                this.parameters,
                this.returnValue,
                this.signature
        )
    }

    private fun DocumentationNode.toField(): KotlinField {
        assert(this.isField) { "node must be a Field or Property" }
        return KotlinField(
                this.simpleName,
                this.qualifiedName,
                this.contentText,
                this.summary.textLength,
                this.modifiers,
                this.type,
                this.signature
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
