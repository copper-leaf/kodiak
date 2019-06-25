package com.copperleaf.kodiak.groovy.models

import com.copperleaf.kodiak.common.ModuleDoc
import com.copperleaf.kodiak.common.fromDocList

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
