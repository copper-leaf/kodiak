package com.copperleaf.kodiak

import java.lang.Runnable

class KotlinClassWithSuperclassAndInterfaces(s1: String) : OpenKotlinClass(s1), KotlinInterface, Runnable {
    override fun doThing() {
        TODO("Not yet implemented")
    }

    override fun run() {
        TODO("Not yet implemented")
    }
}
