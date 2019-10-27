package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.java.models.JavaPackage
import com.sun.javadoc.PackageDoc

fun PackageDoc.toPackageDoc(deep: Boolean): JavaPackage {
    return JavaPackage(
        this,
        this.name(),
        this.name(),
        "", // this will be populated later via .copy()
        emptyList(),
        this.getComment(),
        this.packageSignature(),
        if(deep) this.allClasses().filter { !it.isSuppressed() }.map { it.toClassDoc(false) } else emptyList(),
        emptyList() // this will be populated later via .copy()
    )
}

fun PackageDoc.packageSignature(): List<CommentComponent> {
    return listOf(
        CommentComponent("keyword", "package "),
        CommentComponent(TYPE_NAME, this.name(), this.name())
    )
}
