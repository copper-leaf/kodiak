package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaPackage
import com.copperleaf.json.common.DocComment
import com.sun.javadoc.PackageDoc

fun PackageDoc.toPackageDoc(): JavaPackage {
    return JavaPackage(
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