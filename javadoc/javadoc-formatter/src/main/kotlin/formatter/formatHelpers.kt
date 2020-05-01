package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.CommentTag
import com.copperleaf.kodiak.common.DocComment
import com.sun.javadoc.ClassDoc
import com.sun.javadoc.Doc
import com.sun.javadoc.SeeTag
import com.sun.javadoc.Tag

fun Doc.isSuppressed(): Boolean {
    return this.tags().any { it.kind() in listOf("@suppress") }
}

fun Doc.getComment(): DocComment {
    return DocComment(
        this.inlineTags().asCommentComponents(),
        this.tags().asCommentComponentsMap()
    )
}

fun Tag?.getComment(commentTrim: String = ""): DocComment {
    return DocComment(
        if (this != null) arrayOf(this).asCommentComponents(commentTrim) else emptyList(),
        if (this != null) arrayOf(this).asCommentComponentsMap(commentTrim) else emptyMap()
    )
}

fun Array<Tag>.asCommentComponents(commentTrim: String = ""): List<CommentComponent> {
    return this.flatMap { it.toCommentComponent(commentTrim) }
}

fun Array<Tag>.asCommentComponentsMap(commentTrim: String = ""): Map<String, CommentTag> {
    return this.map { tag ->
        val commentComponent = tag.toCommentComponent(commentTrim).first()
        commentComponent.kind to commentComponent.toCommentTag()
    }.toMap()
}

fun Tag.toCommentComponent(commentTrim: String = ""): List<CommentComponent> {
    val key: String
    val text: String
    val value: String
    if (this is SeeTag && referencedClass() != null) {
        key = TYPE_NAME
        text = referencedClass().simpleTypeName()
        value = referencedClass().qualifiedTypeName()
    } else {
        key = kind().let { if (it.startsWith("@")) it.drop(1) else it }
        text = text().trim().removePrefix(commentTrim).trim()
        value = ""
    }
    return CommentComponent(
        key,
        text,
        value
    ).expandUnparsedInlineTags()
}

fun CommentComponent.toCommentTag(): CommentTag {
    return CommentTag(listOf(this))
}

fun List<String>.toModifierListSignature(): List<CommentComponent> {
    return this.map { CommentComponent(TEXT, "$it ") }
}

fun CommentComponent.expandUnparsedInlineTags(): List<CommentComponent> {

    val regex = "\\{@(\\w+)(.*?)\\}".toRegex()

    if (regex.containsMatchIn(this.text)) {
        val matches = regex.findAll(this.text)

        val expanded = mutableListOf<CommentComponent>()

        var lastIndex = 0
        for (match in matches) {
            val (_, tagType, tagValue) = match.groupValues
            expanded += CommentComponent(
                TEXT,
                this.text.substring(lastIndex, match.range.first),
                ""
            )
            expanded += CommentComponent(
                if (tagType == "link") TYPE_NAME else tagType,
                tagValue.trim(),
                tagValue.trim()
            )
            lastIndex = match.range.last + 1
        }

        expanded += CommentComponent(
            TEXT,
            this.text.substring(lastIndex).trim(),
            ""
        )

        return expanded
    } else {
        return listOf(this)
    }
}

fun ClassDoc.asCommentComponent() : CommentComponent {
    return CommentComponent(TYPE_NAME, this.typeName(), this.qualifiedTypeName())
}
