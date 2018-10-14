package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.CommentTag
import com.sun.javadoc.SeeTag
import com.sun.javadoc.Tag

fun Array<Tag>.asCommentTags(): List<CommentTag> {
    return this.map(Tag::toCommentTag)
}

fun Array<Tag>.asCommentTagsMap(): Map<String, CommentTag> {
    return this.map { tag ->
        val commentTag = tag.toCommentTag()
        commentTag.kind to commentTag
    }.toMap()
}

fun Tag.toCommentTag(): CommentTag {
    val key = kind().let { if (it.startsWith("@")) it.drop(1) else it }
    val name: String
    val qualifiedName: String
    if(this is SeeTag) {
        name = referencedClass().simpleTypeName()
        qualifiedName = referencedClass().qualifiedTypeName()
    }
    else {
        name = text()
        qualifiedName = text()
    }

    return CommentTag(
            key,
            name,
            qualifiedName
    )
}