package com.copperleaf.dokka.json.models

/**
 * This represents the root of your site, containing a list of the available classes and packages.
 */
data class KotlinRootDoc(
        val classes: List<KotlinClassDoc>,
        val packages: List<KotlinPackageDoc>
)

/**
 * The docs for a single class. Includes a list of the fields and methods in the class, as well as the KDoc comment on
 * the class.
 */
data class KotlinClassDoc(
        val comment: String,
        val fields: List<KotlinFieldDoc>,
        val methods: List<KotlinMethodDoc>
)

/**
 * The docs for a single package. Includes a list of the classes in the package, as well as the KDoc comment on the
 * package.
 */
data class KotlinPackageDoc(
        val comment: String,
        val classes: List<KotlinClassDoc>
)

/**
 * The docs for a field in a class. Includes docs for the its annotations, and its comment.
 */
data class KotlinFieldDoc(
        val comment: String,
        val tags: Map<String, String>
)

/**
 * The docs for a method in a class. Includes docs for the return type, its parameters, its annotations, and its
 * comments.
 */
data class KotlinMethodDoc(
        val comment: String,
        val parameters: Map<String, String>
)


