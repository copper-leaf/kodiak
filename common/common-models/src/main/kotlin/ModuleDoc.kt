package com.copperleaf.kodiak.common

interface ModuleDoc : AutoDocument {
    fun roots(): List<DocElement>
}
