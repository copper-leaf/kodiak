package com.copperleaf.dokka.json.generator.formatter

import com.caseyjbrooks.clog.Clog
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.CommentTag
import com.copperleaf.json.common.DocComment
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
import org.jetbrains.dokka.NodeKind

fun DocumentationNode.getComment(): DocComment {
    return DocComment(
        this.contentText,
        this.contentTags
    )
}

fun DocumentationNode.getComment(it: DocumentationNode, sectionName: String, subjectName: String?): DocComment {
    return DocComment(
        it.contentText(sectionName, subjectName),
        this.contentTags
    )
}

val DocumentationNode.contentText: List<CommentComponent> get() = DokkaContentFormatter(this).extractContent()
fun DocumentationNode.contentText(sectionName: String, subjectName: String?): List<CommentComponent> =
    DokkaContentFormatter(this).extractContentFromSection(sectionName, subjectName)

val DocumentationNode.contentTags: Map<String, CommentTag> get() = emptyMap()

@Suppress("UNUSED_PARAMETER")
class DokkaContentFormatter(val node: DocumentationNode) {

    fun extractContentFromSection(sectionName: String, subjectName: String?): List<CommentComponent> {
        for (section in node.owner!!.content.sections) {
            if (section.tag == sectionName && section.subjectName == subjectName) {
                return listOf(CommentComponent(CommentComponent.TEXT, extractContent(section.children)))
            }
        }

        return listOf(CommentComponent(CommentComponent.TEXT, ""))
    }

    fun extractContent(): List<CommentComponent> {
        val content = this.extractContent(node.content.children)
        return listOf(CommentComponent(CommentComponent.TEXT, content))
    }

    private fun extractContent(content: List<ContentNode>): String {
        return content.mapIndexed { index, it -> extractContent(it, topLevel = index == 0) }.joinToString("")
    }

    private fun extractContent(content: ContentNode, topLevel: Boolean): String {
        when (content) {
            is ContentKeyword               -> return content.format(topLevel)
            is ContentIdentifier            -> return content.format(topLevel)
            is ContentSymbol                -> return content.format(topLevel)
            is ContentEntity                -> return content.format(topLevel)

            is ContentNonBreakingSpace      -> return content.format(topLevel)
            is ContentSoftLineBreak         -> return content.format(topLevel)
            is ContentIndentedSoftLineBreak -> return content.format(topLevel)
            is ContentHardLineBreak         -> return content.format(topLevel)

            is ContentEmphasis              -> return content.format(topLevel)
            is ContentStrong                -> return content.format(topLevel)
            is ContentStrikethrough         -> return content.format(topLevel)

            is ContentCode                  -> return content.format(topLevel)
            is ContentBlockCode             -> return content.format(topLevel)

            is ContentNodeDirectLink        -> return content.format(topLevel)
            is ContentNodeLazyLink          -> return content.format(topLevel)
            is NodeRenderContent            -> return content.format(topLevel)
            is ContentExternalLink          -> return content.format(topLevel)
            is ContentNodeLink              -> return content.format(topLevel)

            is ContentUnorderedList         -> return content.format(topLevel)
            is ContentOrderedList           -> return content.format(topLevel)
            is ContentListItem              -> return content.format(topLevel)

            is ContentHeading               -> return content.format(topLevel)
            is ContentSection               -> return content.format(topLevel)
            is ContentParagraph             -> return content.format(topLevel)

            is ContentText                  -> return content.format(topLevel)
            is ContentBlock                 -> return content.format(topLevel)
            is ContentEmpty                 -> return content.format(topLevel)

            else                            -> Clog.e("Unhandled content node: $content (${content.javaClass})")
        }
        return ""
    }

// Content Node typeName Handlers
//----------------------------------------------------------------------------------------------------------------------

    private fun ContentEmpty.format(topLevel: Boolean): String {
        return ""
    }

    private fun ContentBlock.format(topLevel: Boolean): String {
        return joinChildren(this)
    }

    private fun ContentText.format(topLevel: Boolean): String {
        return this.text
    }

    private fun ContentKeyword.format(topLevel: Boolean): String {
        return this.text
    }

    private fun ContentIdentifier.format(topLevel: Boolean): String {
        return this.text
    }

    private fun ContentSymbol.format(topLevel: Boolean): String {
        return this.text
    }

    private fun ContentEntity.format(topLevel: Boolean): String {
        return this.text
    }

    private fun ContentNonBreakingSpace.format(topLevel: Boolean): String {
        return "&nbsp;"
    }

    private fun ContentSoftLineBreak.format(topLevel: Boolean): String {
        return "<br>"
    }

    private fun ContentIndentedSoftLineBreak.format(topLevel: Boolean): String {
        return "  <br>"
    }

    private fun ContentParagraph.format(topLevel: Boolean): String {
        return if (topLevel) joinChildren(this) else wrap("<p>", "</p>", joinChildren(this))
    }

    private fun ContentEmphasis.format(topLevel: Boolean): String {
        return if (topLevel) joinChildren(this) else wrap("<i>", "</i>", joinChildren(this))
    }

    private fun ContentStrong.format(topLevel: Boolean): String {
        return if (topLevel) joinChildren(this) else wrap("<b>", "</b>", joinChildren(this))
    }

    private fun ContentStrikethrough.format(topLevel: Boolean): String {
        return if (topLevel) joinChildren(this) else wrap("<del>", "</del>", joinChildren(this))
    }

    private fun ContentCode.format(topLevel: Boolean): String {
        return if (topLevel) joinChildren(this) else wrap("<code>", "</code>", joinChildren(this))
    }

    private fun ContentBlockCode.format(topLevel: Boolean): String {
        return if (topLevel) joinChildren(this) else wrap(
            "<pre><code class=\"language-${this.language}\">",
            "</code></pre>",
            joinChildren(this)
        )
    }

    private fun ContentNodeLink.format(topLevel: Boolean): String {
        return joinChildren(this)
    }

    private fun ContentHardLineBreak.format(topLevel: Boolean): String {
        return "<br>"
    }

    private fun ContentNodeDirectLink.format(topLevel: Boolean): String {
        return joinChildren(this)
    }

    private fun ContentNodeLazyLink.format(topLevel: Boolean): String {
        return joinChildren(this)
    }

    private fun NodeRenderContent.format(topLevel: Boolean): String {
        return extractContent(listOf(KotlinLanguageService().render(node, mode)))
    }

    private fun ContentExternalLink.format(topLevel: Boolean): String {
        return wrap("<a href=\"${this.href}\">", "</a>", joinChildren(this))
    }

    private fun ContentUnorderedList.format(topLevel: Boolean): String {
        return wrap("<ul>", "</ul>", joinChildren(this))
    }

    private fun ContentOrderedList.format(topLevel: Boolean): String {
        return wrap("<ol>", "</ol>", joinChildren(this))
    }

    private fun ContentListItem.format(topLevel: Boolean): String {
        val child = this.children.singleOrNull()
        return if (child is ContentParagraph) {
            // Ignore paragraph if item is nested underneath an item
            wrap("<li>", "</li>", joinChildren(child))
        } else {
            wrap("<li>", "</li>", joinChildren(this))
        }
    }

    private fun ContentHeading.format(topLevel: Boolean): String {
        return wrap("<h${this.level}>", "</h${this.level}>", joinChildren(this))
    }

    private fun ContentSection.format(topLevel: Boolean): String {
        return wrap("<${this.tag}>", "</${this.tag}>", joinChildren(this))
    }

// Helper Methods
//----------------------------------------------------------------------------------------------------------------------

    private fun wrap(prefix: String, suffix: String, body: String): String = "$prefix$body$suffix"

    private fun joinChildren(block: ContentBlock): String =
        block.children.joinToString("") { extractContent(it, topLevel = false) }

}






/*

class NodeRenderContent(
    val node: DocumentationNode,
    val mode: LanguageService.RenderMode
): ContentNode {
    override val textLength: Int
        get() = 0 //TODO: Clarify?
}

class LazyContentBlock(private val fillChildren: () -> List<ContentNode>) : ContentBlock() {
    private var computed = false
    override val children: ArrayList<ContentNode>
        get() {
            if (!computed) {
                computed = true
                children.addAll(fillChildren())
            }
            return super.children
        }

    override fun equals(other: Any?): Boolean {
        return other is LazyContentBlock && other.fillChildren == fillChildren && super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode() + 31 * fillChildren.hashCode()
    }
}

class ContentBlockSampleCode(language: String = "kotlin", val importsBlock: ContentBlockCode = ContentBlockCode(language)) : ContentBlockCode(language)

data class ContentBookmark(val name: String): ContentBlock()
data class ContentLocalLink(val href: String) : ContentBlock()

 */