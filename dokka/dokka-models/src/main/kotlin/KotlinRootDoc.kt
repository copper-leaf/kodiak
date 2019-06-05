package com.copperleaf.dokka.json.models

/**
 * The result of executing Dokka and transforming the results to JSON.
 */
class KotlinRootDoc(
    val packages: List<KotlinPackage>,
    val classes: List<KotlinClass>
)
