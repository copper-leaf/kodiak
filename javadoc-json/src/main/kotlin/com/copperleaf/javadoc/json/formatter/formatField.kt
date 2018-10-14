package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaField
import com.sun.javadoc.FieldDoc

fun FieldDoc.toField(): JavaField {
    return JavaField(
            this,
            this.name(),
            this.qualifiedName(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
            listOf(this.modifiers()),
            this.type().simpleTypeName(),
            this.type().qualifiedTypeName(),
            emptyList()
    )
}