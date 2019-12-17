package com.copperleaf.kodiak.common

// -- VERSION --
const val version = "0.4.4"
// -- ENDVERSION --

internal inline fun <T : Any?, U : Any?> List<T>.firstBy(mapper: (T)->U) : U {
    return firstBy(mapper) { it != null }
}

internal inline fun <T : Any?, U : Any?> List<T>.firstBy(mapper: (T)->U, predicate: (U)->Boolean) : U {
    for(item in this) {
        val result = mapper(item)
        if(predicate(result)) return result
    }
    throw NoSuchElementException("Collection contains no mapped element matching the predicate.")
}

internal inline fun <T : Any?, U : Any?> List<T>.firstOrNullBy(mapper: (T)->U) : U? {
    return firstOrNullBy(mapper) { it != null }
}

internal inline fun <T : Any?, U : Any?> List<T>.firstOrNullBy(mapper: (T)->U, predicate: (U)->Boolean) : U? {
    for(item in this) {
        val result = mapper(item)
        if(predicate(result)) return result
    }
    return null
}
