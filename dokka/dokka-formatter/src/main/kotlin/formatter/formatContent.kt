package com.copperleaf.kodiak.kotlin.formatter

import clog.Clog
import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.CommentTag
import com.copperleaf.kodiak.common.DocComment
import org.jetbrains.dokka.ContentBlock
import org.jetbrains.dokka.ContentBlockCode
import org.jetbrains.dokka.ContentCode
import org.jetbrains.dokka.ContentEmphasis
import org.jetbrains.dokka.ContentEmpty
import org.jetbrains.dokka.ContentEntity
import org.jetbrains.dokka.ContentExternalLink
import org.jetbrains.dokka.ContentHardLineBreak
import org.jetbrains.dokka.ContentHeading
import org.jetbrains.dokka.ContentIdentifier
import org.jetbrains.dokka.ContentIndentedSoftLineBreak
import org.jetbrains.dokka.ContentKeyword
import org.jetbrains.dokka.ContentListItem
import org.jetbrains.dokka.ContentNode
import org.jetbrains.dokka.ContentNodeDirectLink
import org.jetbrains.dokka.ContentNodeLazyLink
import org.jetbrains.dokka.ContentNodeLink
import org.jetbrains.dokka.ContentNonBreakingSpace
import org.jetbrains.dokka.ContentOrderedList
import org.jetbrains.dokka.ContentParagraph
import org.jetbrains.dokka.ContentSection
import org.jetbrains.dokka.ContentSoftLineBreak
import org.jetbrains.dokka.ContentStrikethrough
import org.jetbrains.dokka.ContentStrong
import org.jetbrains.dokka.ContentSymbol
import org.jetbrains.dokka.ContentText
import org.jetbrains.dokka.ContentUnorderedList
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeRenderContent
import org.jetbrains.dokka.KotlinLanguageService

fun DocumentationNode.getComment(): DocComment {
    val formatter = DokkaContentFormatter(this).apply { extractContent() }
    return DocComment(
        formatter.components,
        formatter.tags
    )
}

fun DocumentationNode.getComment(sectionName: String, subjectName: String?): DocComment {
    val formatter = DokkaContentFormatter(this).apply { extractContentFromSection(sectionName, subjectName) }
    return DocComment(
        formatter.components,
        formatter.tags
    )
}

@Suppress("UNUSED_PARAMETER")
class DokkaContentFormatter(val node: DocumentationNode) {

    private var used = false
    val components = mutableListOf<RichTextComponent>()
    val tags = mutableMapOf<String, CommentTag>()

    var currentText = ""

    fun extractContentFromSection(sectionName: String, subjectName: String?) {
        check(!used) { "DokkaContentFormatter cannot be reused!" }
        used = true

        for (section in node.owner!!.content.sections) {
            if (section.tag == sectionName && section.subjectName == subjectName) {
                extractContentInternal(section.children)
                components.add(RichTextComponent(TEXT, currentText))
                return
            }
        }
        components.add(RichTextComponent(TEXT, ""))
    }

    fun extractContent() {
        check(!used) { "DokkaContentFormatter cannot be reused!" }
        used = true

        this.extractContentInternal(node.content.children)
        components.add(RichTextComponent(TEXT, currentText))
    }

    private fun extractContentInternal(content: List<ContentNode>) {
        content.mapIndexed { index, it -> extractContentInternal(it, topLevel = index == 0) }
    }

    private fun extractContentInternal(content: ContentNode, topLevel: Boolean) {
        when (content) {
            is ContentKeyword -> content.format(topLevel)
            is ContentIdentifier -> content.format(topLevel)
            is ContentSymbol -> content.format(topLevel)
            is ContentEntity -> content.format(topLevel)

            is ContentNonBreakingSpace -> content.format(topLevel)
            is ContentSoftLineBreak -> content.format(topLevel)
            is ContentIndentedSoftLineBreak -> content.format(topLevel)
            is ContentHardLineBreak -> content.format(topLevel)

            is ContentEmphasis -> content.format(topLevel)
            is ContentStrong -> content.format(topLevel)
            is ContentStrikethrough -> content.format(topLevel)

            is ContentCode -> content.format(topLevel)
            is ContentBlockCode -> content.format(topLevel)

            is ContentNodeDirectLink -> content.format(topLevel)
            is ContentNodeLazyLink -> content.format(topLevel)
            is NodeRenderContent -> content.format(topLevel)
            is ContentExternalLink -> content.format(topLevel)
            is ContentNodeLink -> content.format(topLevel)

            is ContentUnorderedList -> content.format(topLevel)
            is ContentOrderedList -> content.format(topLevel)
            is ContentListItem -> content.format(topLevel)

            is ContentHeading -> content.format(topLevel)
            is ContentSection -> content.format(topLevel)
            is ContentParagraph -> content.format(topLevel)

            is ContentText -> content.format(topLevel)
            is ContentBlock -> content.format(topLevel)
            is ContentEmpty -> content.format(topLevel)

            else -> {
                Clog.e("Unhandled content node: $content (${content.javaClass})")
            }
        }
    }

// Content Node typeName Handlers
// ----------------------------------------------------------------------------------------------------------------------

    private fun ContentEmpty.format(topLevel: Boolean) {
        append("")
    }

    private fun ContentBlock.format(topLevel: Boolean) {
        joinChildren(this)
    }

    private fun ContentText.format(topLevel: Boolean) {
        append(this.text)
    }

    private fun ContentKeyword.format(topLevel: Boolean) {
        append(this.text)
    }

    private fun ContentIdentifier.format(topLevel: Boolean) {
        append(this.text)
    }

    private fun ContentSymbol.format(topLevel: Boolean) {
        append(this.text)
    }

    private fun ContentEntity.format(topLevel: Boolean) {
        append(this.text)
    }

    private fun ContentNonBreakingSpace.format(topLevel: Boolean) {
        append("&nbsp;")
    }

    private fun ContentSoftLineBreak.format(topLevel: Boolean) {
        append("<br>")
    }

    private fun ContentIndentedSoftLineBreak.format(topLevel: Boolean) {
        append("  <br>")
    }

    private fun ContentParagraph.format(topLevel: Boolean) {
        wrap("<p>", "</p>") { joinChildren(this) }
    }

    private fun ContentEmphasis.format(topLevel: Boolean) {
        wrap("<i>", "</i>") { joinChildren(this) }
    }

    private fun ContentStrong.format(topLevel: Boolean) {
        wrap("<b>", "</b>") { joinChildren(this) }
    }

    private fun ContentStrikethrough.format(topLevel: Boolean) {
        wrap("<del>", "</del>") { joinChildren(this) }
    }

    private fun ContentCode.format(topLevel: Boolean) {
        wrap("<code>", "</code>") { joinChildren(this) }
    }

    private fun ContentBlockCode.format(topLevel: Boolean) {
        wrap(
            if (this.language.isNotBlank())
                "<pre class=\"language-${this.language}\"><code class=\"language-${this.language}\">"
            else
                "<pre><code>",
            "</code></pre>"
        ) { joinChildren(this) }
    }

    private fun ContentNodeLink.format(topLevel: Boolean) {
        if (node != null) {
            components.add(RichTextComponent(TEXT, currentText, "")) // append current text to components list
            currentText = "" // get fresh text content
            joinChildren(this) // have children append themselved to the context
            components // convert that text to the text of the link component
                .add(RichTextComponent(TYPE_NAME, currentText, node!!.qualifiedName))
            currentText = "" // prepare to continue with later components
        } else {
            joinChildren(this)
        }
    }

    private fun ContentHardLineBreak.format(topLevel: Boolean) {
        append("<br>")
    }

    private fun NodeRenderContent.format(topLevel: Boolean) {
        extractContentInternal(listOf(KotlinLanguageService().render(node, mode)))
    }

    private fun ContentExternalLink.format(topLevel: Boolean) {
        wrap("<a href=\"${this.href}\">", "</a>") { joinChildren(this) }
    }

    private fun ContentUnorderedList.format(topLevel: Boolean) {
        wrap("<ul>", "</ul>") { joinChildren(this) }
    }

    private fun ContentOrderedList.format(topLevel: Boolean) {
        wrap("<ol>", "</ol>") { joinChildren(this) }
    }

    private fun ContentListItem.format(topLevel: Boolean) {
        val child = this.children.singleOrNull()
        if (child is ContentParagraph) {
            // Ignore paragraph if item is nested underneath an item
            wrap("<li>", "</li>") { joinChildren(child) }
        } else {
            wrap("<li>", "</li>") { joinChildren(this) }
        }
    }

    private fun ContentHeading.format(topLevel: Boolean) {
        return wrap("<h${this.level}>", "</h${this.level}>") { joinChildren(this) }
    }

    private fun ContentSection.format(topLevel: Boolean) {
        wrap("<${this.tag}>", "</${this.tag}>") { joinChildren(this) }
    }

// Helper Methods
// ----------------------------------------------------------------------------------------------------------------------

    private fun append(text: String) {
        currentText += text
    }
    private fun wrap(prefix: String, suffix: String, body: () -> Unit) {
        append(prefix)
        body()
        append(suffix)
    }

    private fun joinChildren(block: ContentBlock) {
        block.children.forEach { extractContentInternal(it, topLevel = false) }
    }
}
