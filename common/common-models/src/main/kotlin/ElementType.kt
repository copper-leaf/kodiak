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
}
