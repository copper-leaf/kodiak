package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.DocComment
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyDoc
import org.codehaus.groovy.groovydoc.GroovyParameter
import org.codehaus.groovy.groovydoc.GroovyTag
import org.codehaus.groovy.groovydoc.GroovyType
import org.codehaus.groovy.tools.groovydoc.ExternalGroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyTag
import org.jsoup.Jsoup
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.select.NodeFilter

fun GroovyDoc.isSuppressed(): Boolean {
    return this.findCommentTags().any { it.name() in listOf("suppress") }
}

fun List<String>.toModifierListSignature(): List<CommentComponent> {
    return this.map { CommentComponent("modifier", "$it ") }
}

fun GroovyType.real(): GroovyType {
    return if (this is ExternalGroovyClassDoc) {
        ExternalGroovyClassDocWrapper(this)
    } else {
        this
    }
}

class ExternalGroovyClassDocWrapper(val ext: ExternalGroovyClassDoc) : GroovyType {
    override fun qualifiedTypeName() = ext.externalClass().name
    override fun simpleTypeName() = ext.externalClass().simpleName
    override fun typeName() = ext.externalClass().simpleName
    override fun isPrimitive() = ext.isPrimitive
}

fun GroovyParameter.realType(): GroovyType {
    return if (this.type() == null) {
        PrimitiveFieldTypeWrapper(this)
    } else {
        this.type()
    }
}

class PrimitiveFieldTypeWrapper(val field: GroovyParameter) : GroovyType {
    override fun qualifiedTypeName() = field.typeName()
    override fun simpleTypeName() = field.typeName()
    override fun typeName() = field.typeName()
    override fun isPrimitive() = true
}

// Determine if class is exception class
//----------------------------------------------------------------------------------------------------------------------

tailrec fun GroovyClassDoc.isExceptionClass(): Boolean {
    return when {
        this.qualifiedTypeName() == Throwable::class.java.name -> true
        this.superclass() != null -> this.superclass().isExceptionClass()
        else -> false
    }
}

// Process comment text
//----------------------------------------------------------------------------------------------------------------------

data class GroovyCommentData(
    val commentText: List<CommentComponent>,
    val commentTags: Map<String, List<Pair<String, String>>>
)

fun String.parseCommentToValues(): GroovyCommentData {
    val commentTags = mutableMapOf<String, List<Pair<String, String>>>()

    val commentDoc = Jsoup.parse(this.trim())
    commentDoc.outputSettings(Document.OutputSettings().apply {
        indentAmount(2)
        prettyPrint(true)
        outline(true)
    })
    commentDoc.filter(object : NodeFilter {
        override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
            return if (node is Comment) {
                NodeFilter.FilterResult.REMOVE
            } else NodeFilter.FilterResult.CONTINUE
        }

        override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
            return if (node is Comment) {
                NodeFilter.FilterResult.REMOVE
            } else NodeFilter.FilterResult.CONTINUE
        }
    })

    val dlElements = commentDoc.select("dl")
    commentDoc.select("dl").remove()

    for (dl in dlElements) {
        val dlName = dl.select("dt").text().trim().removeSuffix(":").toLowerCase()

        val ddElements = dl.select("dd")

        val tagValues = mutableListOf<Pair<String, String>>()
        for (dd in ddElements) {
            val ddName = dd.select("code").text().trim()
            dd.select("code").remove()
            val ddText = dd.html().trimLines().removePrefix("- ")

            tagValues.add(Pair(ddName, ddText))
        }

        commentTags[dlName] = tagValues
    }

    val commentComponents = mutableListOf<CommentComponent>()

    val originalCommentText = commentDoc.select("body").html().trimLines()
    val linkRegex = "<a(.*?)>(.*?)</a>".toRegex()

    var currentIndex = 0

    linkRegex.findAll(originalCommentText).forEach { match ->
        val (wholeMatch, attrs, text) = match.groupValues
        val aTag = Jsoup.parseBodyFragment(wholeMatch).select("a")
        val href = aTag.attr("href")
        val title = aTag.attr("title")

        if(!(href.startsWith("http://") || href.startsWith("https://"))) {
            // internal link, add it as a TYPE_NAME commentComponent
            commentComponents.add(
                CommentComponent(
                    TEXT,
                    originalCommentText.substring(currentIndex, match.range.first),
                    ""
                )
            )

            val linkedId = href.replace("../", "").replace(".html", "").replace("/", ".")
            commentComponents.add(
                CommentComponent(
                    TYPE_NAME,
                    text,
                    linkedId
                )
            )
        }

        currentIndex = match.range.last + 1
    }

    commentComponents.add(
        CommentComponent(
            TEXT,
            originalCommentText.substring(currentIndex),
            ""
        )
    )

    return GroovyCommentData(
        commentComponents,
        commentTags
    )
}

fun GroovyDoc.getComment(): DocComment {
    return DocComment(
        this.findCommentText(),
        emptyMap()
    )
}

fun GroovyTag?.getComment(): DocComment {
    return DocComment(
        (this?.text() ?: "").parseCommentToValues().commentText,
        emptyMap()
    )
}

fun GroovyDoc.findCommentText(): List<CommentComponent> {
    return this.commentText().parseCommentToValues().commentText
}

fun GroovyDoc.findCommentTags(): List<GroovyTag> {
    val groovyTagsList = mutableListOf<GroovyTag>()

    if (this is SimpleGroovyDoc && this.tags() != null) {
        groovyTagsList.addAll(this.tags())
    }

    this.commentText().parseCommentToValues().commentTags.forEach { tagName, values ->
        if (values.any { it.first.isNotBlank() }) {
            // convert values into tags with name
            values.forEach {
                groovyTagsList.add(SimpleGroovyTag(tagName, it.first, it.second))
            }
        } else if (values.size == 1) {
            // use the tagName and single item value only
            groovyTagsList.add(SimpleGroovyTag(tagName, null, values.single().second))
        }
    }

    return groovyTagsList
}

private fun String.trimLines() = this
    .lines()
    .map { it.trimEnd() }
    .joinToString("\n")
