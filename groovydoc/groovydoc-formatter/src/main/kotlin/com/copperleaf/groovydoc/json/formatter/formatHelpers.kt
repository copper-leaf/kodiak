package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.SignatureComponent
import org.codehaus.groovy.groovydoc.GroovyParameter
import org.codehaus.groovy.groovydoc.GroovyType
import org.codehaus.groovy.tools.groovydoc.ExternalGroovyClassDoc

fun List<String>.toModifierListSignature(): List<SignatureComponent> {
    return this.map { SignatureComponent("modifier", "$it ", "") }
}

fun GroovyType.real(): GroovyType {
    return if (this is ExternalGroovyClassDoc) {
        ExternalGroovyClassDocWrapper(this)
    } else {
        this
    }
}

class ExternalGroovyClassDocWrapper(val ext: ExternalGroovyClassDoc) : GroovyType {
    override fun qualifiedTypeName() = ext.externalClass().name
    override fun simpleTypeName() = ext.externalClass().simpleName
    override fun typeName() = ext.externalClass().simpleName
    override fun isPrimitive() = ext.isPrimitive
}

fun GroovyParameter.realType() : GroovyType {
    return if(this.type() == null) {
        PrimitiveFieldTypeWrapper(this)
    }
    else {
        this.type()
    }
}

class PrimitiveFieldTypeWrapper(val field: GroovyParameter) : GroovyType {
    override fun qualifiedTypeName() = field.typeName()
    override fun simpleTypeName() = field.typeName()
    override fun typeName() = field.typeName()
    override fun isPrimitive() = true
}