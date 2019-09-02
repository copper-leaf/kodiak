package com.copperleaf.kodiak.swift.models

import com.copperleaf.kodiak.common.ModuleDoc
import com.copperleaf.kodiak.common.fromDocList

/**
 * The result of executing Swiftdoc and transforming the results to JSON.
 */
class SwiftModuleDoc(
    val sourceFiles: List<SwiftSourceFile>,
    val classes: List<SwiftClass>
) : ModuleDoc {

    override val nodes = listOf(
        fromDocList(::sourceFiles),
        fromDocList(::classes)
    )

}
