package com.copperleaf.groovydoc.json.models

import com.copperleaf.json.common.ModuleDoc
import com.copperleaf.json.common.fromDocList

/**
 * The result of executing Groovydocdoc and transforming the results to JSON.
 */
class GroovyModuleDoc(
    val packages: List<GroovyPackage>,
    val classes: List<GroovyClass>
) : ModuleDoc {

    override val nodes = listOf(
        fromDocList(::packages),
        fromDocList(::classes)
    )
}
