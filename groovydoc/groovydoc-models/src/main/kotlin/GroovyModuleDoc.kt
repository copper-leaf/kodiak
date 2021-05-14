package com.copperleaf.kodiak.groovy.models

import com.copperleaf.kodiak.common.AutoDocumentNode
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.ModuleDoc
import com.copperleaf.kodiak.common.fromDocList

/**
 * The result of executing Groovydocdoc and transforming the results to JSON.
 */
class GroovyModuleDoc(
    val packages: List<GroovyPackage>,
    val classes: List<GroovyClass>
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
