package com.copperleaf.kodiak.common

interface DocElement {
    /**
     * The kind this element represents (class, package, namespace, method, etc.). Typically matches the classname of
     * the element, but may be dynamically created to allow for sub-classes of element kinds (_exception_ classes,
     * etc.).
     */
    val kind: String

    /**
     * The human-readable name of this element.
     */
    val name: String

    /**
     * The unique identifier of this element. Other elements may refer to this specific element with just this ID.
     */
    val id: String

    /**
     * A list of modifiers placed on the doc element (visibility, static, etc.).
     */
    val modifiers: List<String>

    /**
     * The comment metadata for this element.
     */
    val comment: DocComment
}
