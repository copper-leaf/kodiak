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

interface TopLevel {

    /**
     * The ID of the parent node(s) which the current node extends from. This is most likely the same node type as the
     * current node. The IDs should be everything that can be considered a "parent" of the current node, through both
     * inheritance and composition, but should only be the _direct_ parents.
     *
     * Examples:
     *      Classes: the immediate superclass, the implemented interfaces. Do not include the superclass of a superclass, etc.
     *      Packages: the parent package. For example, the package `com.eden.kodiak.common` has a parent package of `com.eden.kodiak`.
     */
    val parents: List<RichTextComponent>

    /**
     * The ID of the nodes which the current node "lives" in. This is most likely _not_ the same node type as the
     * current node.
     */
    val contexts: List<RichTextComponent>
}

interface AutoDocumentNode {
    val name: String
    val elements: List<DocElement>
}

fun fromDoc(prop: KProperty<DocElement?>): AutoDocumentNode {
    return object : AutoDocumentNode {
        override val name: String = prop.name
        override val elements by lazy { prop.getter.call()?.let { listOf(it) } ?: emptyList() }
    }
}

fun fromDocList(prop: KProperty<List<DocElement>?>): AutoDocumentNode {
    return object : AutoDocumentNode {
        override val name: String = prop.name
        override val elements by lazy { prop.getter.call() ?: emptyList() }
    }
}
