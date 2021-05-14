package com.copperleaf.kodiak.swift.models

import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.ModuleDoc
import com.copperleaf.kodiak.common.fromDocList

/**
 * The result of executing Swiftdoc and transforming the results to JSON.
 */
class SwiftModuleDoc(
    val sourceFiles: List<SwiftSourceFile>,
    val classes: List<SwiftClass>
) : ModuleDoc {

    override val nodes: List<AutoDocumentNode>
        get() = listOf(
            fromDocList(::sourceFiles),
            fromDocList(::classes)
        )

    override fun roots(): List<DocElement> {
        return emptyList()
    }
}
