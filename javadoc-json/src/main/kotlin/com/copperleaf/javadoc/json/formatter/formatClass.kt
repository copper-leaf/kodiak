package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavadocClassDoc
import com.sun.javadoc.ClassDoc

fun ClassDoc.toClassDoc(): JavadocClassDoc {
    return JavadocClassDoc(
            this,
            this.containingPackage().name(),
            this.classKind,
            this.typeName(),
            this.qualifiedTypeName(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
            emptyList(),
            emptyList(),
            emptyList()
    )
}

val ClassDoc.classKind: String
    get() {
        return when {
            isInterface -> "interface"
            isAnnotationType -> "annotation"
            isException -> "exception"
            isOrdinaryClass -> "class"
            else -> throw IllegalArgumentException("Class kind not found")
        }
    }