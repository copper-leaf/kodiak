package com.copperleaf.dokka.json.models

import org.jetbrains.dokka.NodeKind

/**
 * The docs for a single class. Includes a list of the fields and methods in the class, as well as the KDoc comment on
 * the class.
 */
data class KotlinClassDoc(
        val `package`: String,
        override val kind: String,
        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        val constructors: List<KotlinConstructor>,
        val methods: List<KotlinMethod>,
        val fields: List<KotlinField>
) : KotlinClasslike

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc comment on the
 * package.
 */
data class KotlinPackageDoc(
        val classes: List<KotlinClassDoc>,
        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int
) : KotlinDocElement {
    override val kind = NodeKind.Package.toString()
}

data class KotlinConstructor(
        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        override val modifiers: List<String>
) : KotlinMemberlike {
    override val kind = "Constructor"
}

data class KotlinMethod(
        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        override val modifiers: List<String>
) : KotlinMemberlike {
    override val kind = "Method"
}

data class KotlinField(
        override val name: String,
        override val qualifiedName: String,
        override val comment: String,
        override val summaryPos: Int,
        override val modifiers: List<String>
) : KotlinMemberlike {
    override val kind = "Field"
}