package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinParameter
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedNameFromType

val DocumentationNode.modifiers: List<String> get() = this.details.filter { it.kind == NodeKind.Modifier }.map { it.name }

val DocumentationNode.parameters: List<KotlinParameter>
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

val DocumentationNode.simpleName: String
    get() {
        return path.drop(1).map { it.name }.filter { it.isNotEmpty() }.last().substringAfter('$')
    }

val DocumentationNode.qualifiedName: String
    get() {
        return if(kind == NodeKind.Type) {
            this.qualifiedNameFromType()
        }
        else {
            path.drop(1).map { it.name }.filter { it.isNotEmpty() }.joinToString(".")
        }
    }

val DocumentationNode.simpleType: String
    get() {
        return (
                if (kind == NodeKind.Type)
                    this
                else
                    this.details.firstOrNull { it.kind == NodeKind.Type }
                )
                ?.simpleName ?: ""
    }

val DocumentationNode.qualifiedType: String
    get() {
        return (
                if (kind == NodeKind.Type)
                    this
                else
                    this.details.firstOrNull { it.kind == NodeKind.Type }
                )
                ?.qualifiedName ?: ""
    }