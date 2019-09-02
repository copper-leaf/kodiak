package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentTag
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure

private data class SwiftCommentData(
    val commentText: String,
    val commentTags: Map<String, CommentTag>
)

private fun String.parseCommentToValues(): SwiftCommentData {
    val commentTags = mutableMapOf<String, CommentTag>()

    return SwiftCommentData(
        this.trimLines(),
        commentTags
    )
}

fun SourceKittenSubstructure.getComment(): DocComment {
    return DocComment(
        this.comment.findCommentText(),
        this.comment.findCommentTags()
    )
}

fun String.findCommentText(): List<CommentComponent> {
    return listOf(CommentComponent(CommentComponent.TEXT, parseCommentToValues().commentText))
}

fun String.findCommentTags(): Map<String, CommentTag> {
    return parseCommentToValues().commentTags
}

private fun String.trimLines() = this
    .trimIndent()
    .lines()
    .joinToString("\n") { it.trimEnd() }