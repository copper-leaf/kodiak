package com.copperleaf.javadoc.json.models

/**
 * The result of executing Javadoc and transforming the results to JSON.
 */
class JavaRootDoc(
    val packages: List<JavaPackageDoc>,
    val classes: List<JavaClassDoc>
)
