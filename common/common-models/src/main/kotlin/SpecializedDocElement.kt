package com.copperleaf.kodiak.common

interface SpecializedDocElement : DocElement {
    /**
     * Some kinds may be specialized into several sub-kinds, such as a Java Class doc being either an interface,
     * annotation, or normal class, or a parameter being a method or type parameter. This property is optional, but may
     * help give additional information while keeping a uniform doc structure.
     */
    val subKind: String
}