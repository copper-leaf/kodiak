package com.copperleaf.dokka.json.test.kotlin

/**
 * This is a Kotlin class
 */
@Suppress("UNUSED_PARAMETER")
class KotlinClass(
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
     * This is a property defined in the class body, using an internal type
     */
    var classInternalProperty: KotlinInterface? = null

    /**
     * This is a property defined in the class body, using an internal type
     */
    lateinit var genericProperty: List<(String?)->String>

    /**
     * This is a method defined in the class body
     */
    fun classMethod(): String? {
        return null
    }

    /**
     * This is a method defined in the class body which returns nothing
     */
    fun voidMethod() {

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

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @param param1 This is the param1 for the method
     * @param param2 This is the param2 for the method
     * @return This is the returned string value
     */
    fun classMethodWithInternalParams(param1: KotlinInterface, param2: KotlinMarkdown): KotlinInterface? {
        return null
    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @param param This is the param for the method
     * @return This is the returned string value
     */
    fun classMethodWithDefaultNullableParam(param: String? = "param1"): KotlinInterface? {
        return null
    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @param param This is the param1 for the method
     * @return This is the returned string value
     */
    fun classMethodWithFunctionParameter(param: (String)->String): String? {
        return null
    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @param param This is the param1 for the method
     * @return This is the returned string value
     */
    fun classMethodWithInternalFunctionParameter(param: (KotlinInterface?, (String?, Int)->String?)->KotlinMarkdown?): KotlinMarkdown? {
        return null
    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @param param This is the param1 for the method
     * @return This is the returned string value
     */
    fun classMethodWithReceiverFunctionParameter(param: KotlinInterface.(String?)->KotlinMarkdown?): KotlinMarkdown? {
        return null
    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @param param This is the param1 for the method
     * @return This is the returned string value
     */
    fun classMethodWithGenericReceiverFunctionParameter(param: List<String?>?.(List<String?>?)->List<String?>?): List<String?>? {
        return null
    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @param param This is the param1 for the method
     * @return This is the returned string value
     */
    fun <T, U> multipleTypeParameters(param: Map<T?, U?>) {

    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @receiver This is the receiver for the method
     * @return This is the returned string value
     */
    fun  String?.methodWithReceiver() {

    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @receiver This is the receiver for the method
     * @return This is the returned string value
     */
    fun  List<String>.methodWithReceiverTypeParameter() {

    }

    /**
     * This is a method defined in the class body, which has parameters and a return value with internal types
     *
     * @receiver This is the receiver for the method
     * @return This is the returned string value
     */
    fun  KotlinMarkdown?.methodWithInternalReceiver() {

    }


    /**
     * This is a method which attempts to include all the various doc components at once.
     *
     * @param T the type of T
     * @param U the type of U
     * @param param1 the function param 1
     * @param param2 the function param 2
     * @receiver This is the receiver for the method
     * @return This is the returned string value
     */
    fun <T, U> List<T?>?.jackOfAllTrades(
            param1: List<T?>?.(List<T?>?) -> List<U?>?,
            param2: String
    ): U? {
        return null
    }

}