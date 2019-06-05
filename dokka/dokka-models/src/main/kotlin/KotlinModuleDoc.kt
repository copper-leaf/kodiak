package com.copperleaf.dokka.json.models

import com.copperleaf.json.common.ModuleDoc
import com.copperleaf.json.common.fromDocList

/**
 * The result of executing Dokka and transforming the results to JSON.
 */
class KotlinModuleDoc(
    val packages: List<KotlinPackage>,
    val classes: List<KotlinClass>
) : ModuleDoc {
    override val nodes = listOf(
        fromDocList(::packages),
        fromDocList(::classes)
    )
}
