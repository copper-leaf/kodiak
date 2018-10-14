package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaConstructor
import com.sun.javadoc.ConstructorDoc

fun ConstructorDoc.toConstructor(): JavaConstructor {
    return JavaConstructor(
            this,
            this.name(),
            this.qualifiedName(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
            listOf(this.modifiers()),
            formatParameters(this.parameters(), this.paramTags()),
            emptyList()
    )
}