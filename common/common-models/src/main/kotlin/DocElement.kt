package com.copperleaf.json.common

interface DocElement {
    /**
     * The kind
     */
    val kind: String

    /**
     * The human-readable name of this element
     */
    val name: String

    /**
     * The unique identifier of this element. Other elements may refer to this specific element with just this ID
     */
    val id: String

    /**
     * A list of comment components which, when concatenated together, creates the full inline comment description.
     */
    val commentComponents: List<CommentComponent>

    /**
     * A map of comment tags which represent the metadata for this element, either written in the comment text, or
     * inferred by the documentation tool
     */
    val commentTags: Map<String, CommentTag>
}
