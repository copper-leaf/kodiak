package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaPackage
import com.sun.javadoc.PackageDoc

fun PackageDoc.toPackageDoc(): JavaPackage {
    return JavaPackage(
        this,
        this.name(),
        this.name(),
        emptyList(),
        this.getComment(),
        this.allClasses().map { it.toClassDoc(false) }
    )
}