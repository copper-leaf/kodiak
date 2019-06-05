package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaPackageDoc
import com.copperleaf.json.common.DocComment
import com.sun.javadoc.PackageDoc

fun PackageDoc.toPackageDoc(): JavaPackageDoc {
    return JavaPackageDoc(
        this,
        this.name(),
        this.name(),
        emptyList(),
        DocComment(
            this.inlineTags().asCommentComponents(),
            this.tags().asCommentComponentsMap()
        ),
        this.allClasses().map { it.toClassDoc(false) }
    )
}