package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
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
        this.inlineTags().asRichTextComponents(),
        this.tags().asRichTextComponentsMap()
    )
}

fun Tag?.getComment(commentTrim: String = ""): DocComment {
    return DocComment(
        if (this != null) arrayOf(this).asRichTextComponents(commentTrim) else emptyList(),
        if (this != null) arrayOf(this).asRichTextComponentsMap(commentTrim) else emptyMap()
    )
}

fun Array<Tag>.asRichTextComponents(commentTrim: String = ""): List<RichTextComponent> {
    return this.flatMap { it.toRichTextComponent(commentTrim) }
}

fun Array<Tag>.asRichTextComponentsMap(commentTrim: String = ""): Map<String, CommentTag> {
    return this.map { tag ->
        val RichTextComponent = tag.toRichTextComponent(commentTrim).first()
        RichTextComponent.kind to RichTextComponent.toCommentTag()
    }.toMap()
}

fun Tag.toRichTextComponent(commentTrim: String = ""): List<RichTextComponent> {
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
    return RichTextComponent(
        key,
        text,
        value
    ).expandUnparsedInlineTags()
}

fun RichTextComponent.toCommentTag(): CommentTag {
    return CommentTag(listOf(this))
}

fun List<String>.toModifierListSignature(): List<RichTextComponent> {
    return this.map { RichTextComponent(TEXT, "$it ") }
}

fun RichTextComponent.expandUnparsedInlineTags(): List<RichTextComponent> {

    val regex = "\\{@(\\w+)(.*?)\\}".toRegex()

    if (regex.containsMatchIn(this.text)) {
        val matches = regex.findAll(this.text)

        val expanded = mutableListOf<RichTextComponent>()

        var lastIndex = 0
        for (match in matches) {
            val (_, tagType, tagValue) = match.groupValues
            expanded += RichTextComponent(
                TEXT,
                this.text.substring(lastIndex, match.range.first),
                ""
            )
            expanded += RichTextComponent(
                if (tagType == "link") TYPE_NAME else tagType,
                tagValue.trim(),
                tagValue.trim()
            )
            lastIndex = match.range.last + 1
        }

        expanded += RichTextComponent(
            TEXT,
            this.text.substring(lastIndex).trim(),
            ""
        )

        return expanded
    } else {
        return listOf(this)
    }
}

fun ClassDoc.asRichTextComponent(type: String = TYPE_NAME) : RichTextComponent {
    return RichTextComponent(type, this.typeName(), this.qualifiedTypeName())
}
