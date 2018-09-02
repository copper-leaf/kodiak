package com.copperleaf.dokka.json.test.camelCasePackage

/**
 * This is a Kotlin class
 */
class KotlinClass2(
        /**
         * This is a property defined in the primary constructor
         */
        val constructorProperty: String
) {

    /**
     * This is a property defined in the class body
     */
    var classProperty: String? = null

    /**
     * This is a property defined in the class body, which also defines getters and setters
     */
    var getterSetterProperty: String? = null
        /**
         * This is the getter for getterSetterProperty
         */
        get() = ""

        /**
         * This is the setter for getterSetterProperty
         */
        set(value) {
            println("$value")
            field = value
        }

    /**
     * This is a method defined in the class body
     */
    fun classMethod(): String? {
        return null
    }

    /**
     * This is a method defined in the class body, which has parameters
     *
     * @param param This is the param for the method
     * @return This is the returned string value
     */
    fun classMethodWithParams(param: String): String? {
        return null
    }

}