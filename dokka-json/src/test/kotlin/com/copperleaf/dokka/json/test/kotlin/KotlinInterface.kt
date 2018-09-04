package com.copperleaf.dokka.json.test.kotlin

/**
 * This is a Kotlin interface
 */
interface KotlinInterface {

    /**
     * This is a method defined in the interface body
     */
    fun interfaceMethod(): String

    /**
     * This is a property defined in the interface body
     */
    val interfaceProperty: String

    /**
     * This is a method defined in the interface body, which has parameters
     *
     * @param param This is the param for the method
     * @return This is the returned string value
     */
    fun interfaceMethodWithParams(param: String): String

}