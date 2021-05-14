package com.copperleaf.kodiak.java.models

import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.ModuleDoc
import com.copperleaf.kodiak.common.fromDocList

/**
 * The result of executing Javadoc and transforming the results to JSON.
 */
class JavaModuleDoc(
    val packages: List<JavaPackage>,
    val classes: List<JavaClass>
) : ModuleDoc {

    override val nodes: List<AutoDocumentNode>
        get() = listOf(
            fromDocList(::packages),
            fromDocList(::classes)
        )

    override fun roots(): List<DocElement> {
        return packages.filter { it.parent.isBlank() }
    }
}
