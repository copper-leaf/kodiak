package com.copperleaf.kodiak

class KotlinClassWithSuppressedMembers() {

    /**
     * @suppress
     */
    constructor(dontShowThisConstructor: Int) : this()
    constructor(showThisConstructor: String) : this()

    /**
     * @suppress
     */
    val dontShowThisProperty: Int = 0
    val showThisProperty: String = ""

    /**
     * @suppress
     */
    fun dontShowThisFunction(): Int = 0
    fun showThisFunction(): String = ""
}
