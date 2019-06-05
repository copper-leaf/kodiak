package com.copperleaf.groovydoc.json.models

/**
 * The result of executing Groovydocdoc and transforming the results to JSON.
 */
class GroovyRootDoc(
    val packages: List<GroovyPackage>,
    val classes: List<GroovyClass>
)
