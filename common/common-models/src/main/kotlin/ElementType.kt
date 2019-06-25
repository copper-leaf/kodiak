package com.copperleaf.kodiak.common

interface ElementType : DocElement {

    /**
     * The human-readble type of this element
     */
    val typeName: String

    /**
     * The unique identifier for the type representing this element
     */
    val typeId: String

    /**
     * A simple, rich signature for this type which, when concatenated together, creates the full declaration of this
     * type.
     */
    val signature: List<CommentComponent>
}
