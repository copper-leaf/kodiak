package com.copperleaf.javadoc.json.models

data class KssRootdoc(
    val sections: List<KssSection>
)

data class KssSection(
    val name: String
)