package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocMethod
import com.copperleaf.groovydoc.json.models.GroovydocParameter
import com.copperleaf.groovydoc.json.models.GroovydocReturnType
import com.copperleaf.groovydoc.json.models.GroovydocType
import com.copperleaf.groovydoc.json.models.SignatureComponent
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyMethodDoc.toMethod(parent: GroovyClassDoc): GroovydocMethod {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    val parameters = formatParameters(this.parameters(), this.findCommentTags().filter { it.name() == "parameters" })
    val returnType = this.returnType().real().toReturnType(this)
    return GroovydocMethod(
            this,
            this.name(),
            this.name(),
            this.findCommentText(),
            modifiers,
            parameters,
            returnType,
            this.methodSignature(
                    modifiers,
                    parameters,
                    returnType
            )
    )
}

fun GroovyType.toReturnType(parent: GroovyMethodDoc): GroovydocReturnType {
    return GroovydocReturnType(
            this,
            this.simpleTypeName(),
            this.qualifiedTypeName(),
            parent.findCommentTags().firstOrNull { it.name() == "returns" }?.text() ?: "",
            this.simpleTypeName(),
            this.qualifiedTypeName(),
            this.toTypeSignature()
    )
}

fun GroovyMethodDoc.methodSignature(
    modifiers: List<String>,
    parameters: List<GroovydocParameter>,
    returnType: GroovydocType
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
//    list.addAll(this.typeParameters().toWildcardSignature())
    list.addAll(returnType.signature)
    list.add(SignatureComponent("name", " ${this.name()}", ""))
    list.addAll(parameters.toParameterListSignature())

    return list
}