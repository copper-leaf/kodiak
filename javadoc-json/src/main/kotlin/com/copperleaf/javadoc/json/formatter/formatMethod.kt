package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaMethod
import com.copperleaf.javadoc.json.models.JavaReturnType
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.Tag
import com.sun.javadoc.Type

fun MethodDoc.toMethod(): JavaMethod {
    return JavaMethod(
            this,
            this.name(),
            this.qualifiedName(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
            listOf(this.modifiers()),
            formatParameters(this.parameters(), this.paramTags()),
            this.returnType().toReturnType(this.tags().find { it.name() == "@return" }),
            emptyList()
    )
}

fun Type.toReturnType(returnTag: Tag?): JavaReturnType {
    return JavaReturnType(
            this,
            this.simpleTypeName(),
            this.qualifiedTypeName(),
            returnTag?.text() ?: "",
            if (returnTag != null) arrayOf(returnTag).asCommentTags() else emptyList(),
            if (returnTag != null) arrayOf(returnTag).asCommentTagsMap() else emptyMap(),
            this.simpleTypeName(),
            this.qualifiedTypeName(),
            emptyList()
    )
}
