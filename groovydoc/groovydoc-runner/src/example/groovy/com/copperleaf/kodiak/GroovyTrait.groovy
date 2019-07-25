package com.copperleaf.kodiak

trait GroovyTrait {

    public String stringField = ""
    public GroovyClass groovyClassField = null

    String methodReturningString() {
        return stringField
    }

    GroovyClass methodReturningGroovyClass() {
        return groovyClassField
    }
}
