package com.copperleaf.kodiak.common

import kotlin.reflect.KProperty

/**
 * AutoDocument creates a way for documentation elements to provide information to programmatic tooling, for better
 * automated documentation exploration.
 */
interface AutoDocument {

    /**
     * The list of children nodes of this element. This is metadata only, as it deals with reflection-capabilities for
     * exploring the data. It should always be marked `@Transient` on Serializable elements.
     */
    val nodes: List<AutoDocumentNode>
}

data class AutoDocumentNode(
    val prop: KProperty<Any?>,
    val getter: () -> List<DocElement>
)

fun fromDoc(prop: KProperty<DocElement?>): AutoDocumentNode {
    return AutoDocumentNode(prop) { prop.getter.call()?.let { listOf(it) } ?: emptyList() }
}

fun fromDocList(prop: KProperty<List<DocElement>>): AutoDocumentNode {
    return AutoDocumentNode(prop) { prop.getter.call() }
}
