package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavadocPackageDoc
import com.sun.javadoc.PackageDoc

fun PackageDoc.toPackageDoc(): JavadocPackageDoc {
    return JavadocPackageDoc(
            this,
            this.name(),
            this.name(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
            emptyList()
    )
}