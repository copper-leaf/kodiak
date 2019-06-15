package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
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
        this.superclass() != null                              -> this.superclass().isExceptionClass()
        else                                                   -> false
    }
}

// Process comment text
//----------------------------------------------------------------------------------------------------------------------

private data class GroovyCommentData(
    val commentText: String,
    val commentTags: Map<String, List<Pair<String, String>>>
)

private fun GroovyDoc.parseCommentToValues(): GroovyCommentData {
    val commentTags = mutableMapOf<String, List<Pair<String, String>>>()

    val commentDoc = Jsoup.parse(this.commentText().trim())
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

    return GroovyCommentData(
        commentDoc.select("body").html().trimLines(),
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
        (this?.text() ?: "").asCommentText(),
        emptyMap()
    )
}

fun GroovyDoc.findCommentText(): List<CommentComponent> {
    return listOf(
        CommentComponent(CommentComponent.TEXT, parseCommentToValues().commentText)
    )
}

fun String.asCommentText(): List<CommentComponent> {
    return listOf(
        CommentComponent(CommentComponent.TEXT, this.trim())
    )
}

fun GroovyDoc.findCommentTags(): List<GroovyTag> {
    val groovyTagsList = mutableListOf<GroovyTag>()

    if (this is SimpleGroovyDoc && this.tags() != null) {
        groovyTagsList.addAll(this.tags())
    }

    parseCommentToValues().commentTags.forEach { tagName, values ->
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