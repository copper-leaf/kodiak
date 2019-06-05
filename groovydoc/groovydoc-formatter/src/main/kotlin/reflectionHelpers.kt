package com.copperleaf.groovydoc.json

import com.caseyjbrooks.clog.Clog

fun <T> Any.getFieldValue(fieldName: String): T? {
    return try {
        val hiddenField = this.javaClass.getField(fieldName)
        hiddenField.isAccessible = true
        return hiddenField.get(this) as T
    }
    catch (e: Exception) {
        Clog.e("error getting field {}: {}", fieldName, e.message)
        null
    }
}