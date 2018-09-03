package com.copperleaf.dokka.json.generator

import com.copperleaf.dokka.json.models.KotlinClassDoc
import com.copperleaf.dokka.json.models.KotlinConstructor
import com.copperleaf.dokka.json.models.KotlinField
import com.copperleaf.dokka.json.models.KotlinMethod
import com.copperleaf.dokka.json.models.KotlinPackageDoc
import org.jetbrains.dokka.ContentBlock
import org.jetbrains.dokka.ContentEmpty
import org.jetbrains.dokka.ContentExternalLink
import org.jetbrains.dokka.ContentListItem
import org.jetbrains.dokka.ContentNode
import org.jetbrains.dokka.ContentNodeLink
import org.jetbrains.dokka.ContentParagraph
import org.jetbrains.dokka.ContentText
import org.jetbrains.dokka.ContentUnorderedList
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.FormatService
import org.jetbrains.dokka.FormattedOutputBuilder
import org.jetbrains.dokka.Location
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedName
import org.jetbrains.dokka.simpleName

class DokkaJsonFormatService : FormatService {
    override val extension: String get() = "json"

    override fun createOutputBuilder(to: StringBuilder, location: Location): FormattedOutputBuilder {
        return DokkaJsonFormattedOutputBuilder(to)
    }
}

class DokkaJsonFormattedOutputBuilder(val to: StringBuilder) : FormattedOutputBuilder {
    override fun appendNodes(nodes: Iterable<DocumentationNode>) {
        val node = nodes.first()

        if (node.isClassLike) {
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
                node.members.filter { it.isClassLike }.map { documentationNodeToClassDoc(it, false) },
                node.simpleName(),
                node.qualifiedName(),
                node.contentText,
                node.summary.textLength
        )
    }

    private fun documentationNodeToClassDoc(node: DocumentationNode, deep: Boolean = false): KotlinClassDoc {
        assert(node.isClassLike) { "node must be a Class-like" }

        val constructors: List<KotlinConstructor> = if (deep) documentationNodeToClassDocConstructors(node) else emptyList()
        val methods: List<KotlinMethod> = if (deep) documentationNodeToClassDocMethods(node) else emptyList()
        val fields: List<KotlinField> = if (deep) documentationNodeToClassDocFields(node) else emptyList()

        return KotlinClassDoc(
                node.path.map { it.name }.filterNot { it.isEmpty() }.first(),
                node.kind.toString(),
                node.simpleName(),
                node.qualifiedName(),
                node.contentText,
                node.summary.textLength,
                constructors,
                methods,
                fields
        )
    }

    private fun documentationNodeToClassDocConstructors(node: DocumentationNode): List<KotlinConstructor> {
        assert(node.isClassLike) { "node must be a Class-like" }
        return node.members.filter { it.isConstructor }.map { documentationNodeToConstructor(it) }
    }

    private fun documentationNodeToConstructor(node: DocumentationNode): KotlinConstructor {
        assert(node.isConstructor) { "node must be a Constructor" }
        return KotlinConstructor(
                node.simpleName(),
                node.qualifiedName(),
                node.contentText,
                node.summary.textLength,
                getModifiers(node)
        )
    }

    private fun documentationNodeToClassDocMethods(node: DocumentationNode): List<KotlinMethod> {
        assert(node.isClassLike) { "node must be a Class-like" }
        return node.members.filter { it.isMethod }.map { documentationNodeToMethod(it) }
    }

    private fun documentationNodeToMethod(node: DocumentationNode): KotlinMethod {
        assert(node.isMethod) { "node must be a Function" }
        return KotlinMethod(
                node.simpleName(),
                node.qualifiedName(),
                node.contentText,
                node.summary.textLength,
                getModifiers(node)
        )
    }

    private fun documentationNodeToClassDocFields(node: DocumentationNode): List<KotlinField> {
        assert(node.isClassLike) { "node must be a Class-like" }
        return node.members.filter { it.isField }.map { documentationNodeToField(it) }
    }

    private fun documentationNodeToField(node: DocumentationNode): KotlinField {
        assert(node.isField) { "node must be a Field or Property" }
        return KotlinField(
                node.simpleName(),
                node.qualifiedName(),
                node.contentText,
                node.summary.textLength,
                getModifiers(node)
        )
    }

// Get String text from comments, with help from https://github.com/ScaCap/spring-auto-restdocs/tree/master/spring-auto-restdocs-dokka-json/src/main/kotlin/capital/scalable/dokka/json
//----------------------------------------------------------------------------------------------------------------------

    private val DocumentationNode.contentText: String get() = extractContent(this.content.children)
    private val NodeKind.isClassLike: Boolean get() = NodeKind.classLike.contains(this)
    private val DocumentationNode.isClassLike: Boolean get() = this.kind.isClassLike
    private val DocumentationNode.isConstructor: Boolean get() = this.kind == NodeKind.Constructor
    private val DocumentationNode.isMethod: Boolean get() = this.kind == NodeKind.Function
    private val DocumentationNode.isField: Boolean get() = this.kind == NodeKind.Field || this.kind == NodeKind.Property

    private fun getModifiers(node: DocumentationNode): List<String> {
        return node.details.filter { it.kind == NodeKind.Modifier }.map { it.name }
    }

    private fun extractContent(content: List<ContentNode>): String {
        return content.mapIndexed { index, it -> extractContent(it, topLevel = index == 0) }.joinToString("")
    }

    private fun extractContent(content: ContentNode, topLevel: Boolean): String {
        when (content) {
            is ContentText          -> return content.text
            is ContentUnorderedList -> return wrap("<ul>", "</ul>", joinChildren(content))
            is ContentListItem      -> return listItem(content)
            is ContentParagraph     -> return paragraph(content, topLevel)
            is ContentExternalLink  -> return "<a href=\"${content.href}\">${joinChildren(content)}</a>"
            // Ignore href of references to other code parts and just show the link text, e.g. class name.
            is ContentNodeLink      -> return joinChildren(content)
            // Fallback. Some of the content types above are derived from ContentBlock and
            // thus this one has to be after them.
            is ContentBlock         -> return joinChildren(content)
            is ContentEmpty         -> return ""
            else                    -> println("Unhandled content node: $content")
        }
        return ""
    }

    private fun paragraph(paragraph: ContentParagraph, topLevel: Boolean): String {
        return if (topLevel) {
            // Ignore paragraphs on the top level
            joinChildren(paragraph)
        }
        else {
            wrap("<p>", "</p>", joinChildren(paragraph))
        }
    }

    private fun listItem(item: ContentListItem): String {
        val child = item.children.singleOrNull()
        return if (child is ContentParagraph) {
            // Ignore paragraph if item is nested underneath an item
            wrap("<li>", "</li>", joinChildren(child))
        }
        else {
            wrap("<li>", "</li>", joinChildren(item))
        }
    }

    private fun wrap(prefix: String, suffix: String, body: String): String = "$prefix$body$suffix"

    private fun joinChildren(block: ContentBlock): String = block.children.joinToString("") { extractContent(it, topLevel = false) }

}
