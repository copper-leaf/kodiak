package com.copperleaf.kodiak.swift.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.swift.internal.models.SourceKittenFile
import com.copperleaf.kodiak.swift.models.SwiftClass
import com.copperleaf.kodiak.swift.models.SwiftExtension
import com.copperleaf.kodiak.swift.models.SwiftField
import com.copperleaf.kodiak.swift.models.SwiftMethod
import com.copperleaf.kodiak.swift.models.SwiftSourceFile
import com.copperleaf.kodiak.swift.models.SwiftTypealias
import java.io.File

fun SourceKittenFile.toSourceFile(sourceFileStructures: Map<Class<DocElement>, List<DocElement>>): SwiftSourceFile {
    return SwiftSourceFile(
        this,
        "${File(sourceFile).parentFile?.path ?: ""}/${File(sourceFile).nameWithoutExtension}".trim('/'),
        "${File(sourceFile).parentFile?.path ?: ""}/${File(sourceFile).nameWithoutExtension}".trim('/'),
        emptyList(),
        DocComment(
            emptyList(),
            emptyMap()
        ),
        sourceFileStructures.getAs(SwiftClass::liteCopy),
        sourceFileStructures.getAs(SwiftField::liteCopy),
        sourceFileStructures.getAs(SwiftMethod::liteCopy),
        sourceFileStructures.getAs(SwiftTypealias::liteCopy),
        sourceFileStructures.getAs(SwiftExtension::liteCopy),
        this.sourceFileSignature()
    )
}

fun SourceKittenFile.sourceFileSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent(CommentComponent.TEXT, "Source File"))
    list.add(CommentComponent(CommentComponent.PUNCTUATION, " :"))
    list.add(CommentComponent(CommentComponent.TEXT, " $sourceFile"))

    return list
}

private inline fun <reified T : DocElement> Map<Class<DocElement>, List<DocElement>>.getAs(transform: T.()->T) : List<T> {
    val tt: Class<DocElement> = T::class.java as Class<DocElement>
    return this[tt]?.map { it as T }?.map { it.transform() } ?: emptyList()
}

private fun SwiftClass.liteCopy() : SwiftClass {
    return this.copy(constructors = emptyList(), methods = emptyList(), fields = emptyList())
}

private fun SwiftField.liteCopy() : SwiftField {
    return this.copy()
}

private fun SwiftMethod.liteCopy() : SwiftMethod {
    return this.copy()
}

private fun SwiftTypealias.liteCopy() : SwiftTypealias {
    return this.copy()
}

private fun SwiftExtension.liteCopy() : SwiftExtension {
    return this.copy()
}
