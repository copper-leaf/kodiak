package com.copperleaf.dokka.json.generator

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
import org.jetbrains.dokka.identifierToFilename
import org.jetbrains.dokka.path
import org.jetbrains.dokka.qualifiedName
import org.jetbrains.dokka.simpleName
import org.json.JSONArray
import org.json.JSONObject

class DokkaJsonFormatService : FormatService {
    override val extension: String get() = "json"

    override fun createOutputBuilder(to: StringBuilder, location: Location): FormattedOutputBuilder {
        return DokkaJsonFormattedOutputBuilder(to)
    }
}

class DokkaJsonFormattedOutputBuilder(val to: StringBuilder) : FormattedOutputBuilder {
    override fun appendNodes(nodes: Iterable<DocumentationNode>) {
        val node = nodes.first()
        when {
            NodeKind.classLike.contains(node.kind) -> to.append(mapToJson(nodes.first(), -1).toString(2))
            node.kind == NodeKind.Package          -> to.append(mapToJson(nodes.first(), 1).toString(2))
        }
    }

    private fun mapToJson(node: DocumentationNode, depthRemaining: Int): JSONObject {
        val obj = JSONObject()

        obj.put("name", node.simpleName())
        obj.put("qualifiedName", node.qualifiedName())
        obj.put("kind", node.kind.toString())
        obj.put("classLike", NodeKind.classLike.contains(node.kind))
        obj.put("comment", node.contentText)
        obj.put("summary", node.summaryText)

        if(NodeKind.classLike.contains(node.kind)) {
            obj.put("package", node.path.map { it.name }.map { identifierToFilename(it) }.filterNot { it.isEmpty() }.first())
        }

        // always recurse, for Classdocs
        if (depthRemaining == -1) {
            println("recursing with ${node.members.size} members from ${node.qualifiedName()}")
            obj.put("members", mapToJson(node.members, -1))

        }

        // recurse to a specified depth, for Packagedocs
        else if (depthRemaining > 0) {
            obj.put("members", mapToJson(node.members, depthRemaining - 1))
        }

        return obj
    }

    private fun mapToJson(nodes: Iterable<DocumentationNode>, depthRemaining: Int): JSONArray {
        val arr = JSONArray()
        for (node in nodes) {
            arr.put(mapToJson(node, depthRemaining))
        }
        return arr
    }

    private val DocumentationNode.contentText: String
        get() {
            return extractContent(this.content.children)
        }

    private val DocumentationNode.summaryText: String
        get() {
            return this.contentText.substring(0, this.summary.textLength)
        }

// Get String text from comments, with help from https://github.com/ScaCap/spring-auto-restdocs/tree/master/spring-auto-restdocs-dokka-json/src/main/kotlin/capital/scalable/dokka/json
//----------------------------------------------------------------------------------------------------------------------

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
