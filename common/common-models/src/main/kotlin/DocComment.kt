package com.copperleaf.kodiak.common

import kotlinx.serialization.Serializable

@Serializable
data class DocComment(
    /**
     * A list of comment components which, when concatenated together, creates the full inline comment description.
     */
    val components: List<CommentComponent>,

    /**
     * A map of comment tags which represent the metadata for this element, either written in the comment text, or
     * inferred by the documentation tool
     */
    val tags: Map<String, CommentTag>
)
