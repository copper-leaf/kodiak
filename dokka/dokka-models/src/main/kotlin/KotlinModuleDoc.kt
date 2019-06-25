package com.copperleaf.kodiak.kotlin.models

import com.copperleaf.kodiak.common.ModuleDoc
import com.copperleaf.kodiak.common.fromDocList

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
