package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.java.models.JavaPackage
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