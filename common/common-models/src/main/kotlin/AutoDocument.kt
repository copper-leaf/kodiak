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

interface AutoDocumentNode {
    val name: String
    val elements: List<DocElement>
}

fun fromDoc(prop: KProperty<DocElement?>): AutoDocumentNode {
    return object : AutoDocumentNode {
        override val name: String = prop.name
        override val elements get() = prop.getter.call()?.let { listOf(it) } ?: emptyList()
    }
}

fun fromDocList(prop: KProperty<List<DocElement>>): AutoDocumentNode {
    return object : AutoDocumentNode {
        override val name: String = prop.name
        override val elements get() = prop.getter.call()
    }
}
