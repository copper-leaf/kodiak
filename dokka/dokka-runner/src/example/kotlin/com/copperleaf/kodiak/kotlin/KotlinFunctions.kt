package com.copperleaf.kodiak.kotlin

/**
 * A normal method defined outside of any class, just in the package.
 */
fun packageMethod(input: String): String {
    return input
}

/**
 * An extension method on the KotlinClass class, defined outside of any class, just in the package. The extended typeName is
 * internal to the project.
 */
fun KotlinClass.kotlinClassClassExtensionMethod(input: String): String {
    return input
}

/**
 * An extension method on the String class, defined outside of any class, just in the package. The extended typeName is
 * external to the project.
 */
fun String.stringClassExtensionMethod(input: String): String {
    return input
}

/**
 * A method with a receiver, defined outside of any class, just in the package.
 */
fun methodWithReceiver(input: (String)->Int): Int {
    return input("")
}