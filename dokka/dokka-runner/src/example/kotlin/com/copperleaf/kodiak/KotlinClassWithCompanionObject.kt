package com.copperleaf.kodiak

class KotlinClassWithCompanionObject {

    companion object {
        const val companionObjectVariable: String = ""
        fun companionObjectFunction() : String {
            return companionObjectVariable
        }
    }
}