package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaPackageDoc
import com.sun.javadoc.PackageDoc

fun PackageDoc.toPackageDoc(): JavaPackageDoc {
    return JavaPackageDoc(
            this,
            this.name(),
            this.name(),
            this.commentText(),
            this.inlineTags().asCommentComponents(),
            this.tags().asCommentComponentsMap(),
            this.allClasses().map { it.toClassDoc(false) }
    )
}