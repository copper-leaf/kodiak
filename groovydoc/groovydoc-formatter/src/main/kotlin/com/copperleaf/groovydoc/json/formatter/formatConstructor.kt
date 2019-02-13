package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocConstructor
import com.copperleaf.groovydoc.json.models.GroovydocParameter
import com.copperleaf.groovydoc.json.models.SignatureComponent
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyConstructorDoc

fun GroovyConstructorDoc.toConstructor(parent: GroovyClassDoc): GroovydocConstructor {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    val parameters = formatParameters(this.parameters(), this.findCommentTags().filter { it.name() == "parameters" })
    return GroovydocConstructor(
        this,
        parent.simpleTypeName(),
        parent.qualifiedTypeName(),
        this.findCommentText(),
        modifiers,
        parameters,
        this.constructorSignature(
            parent,
            modifiers,
            parameters
        )
    )
}

fun GroovyConstructorDoc.constructorSignature(
    parent: GroovyClassDoc,
    modifiers: List<String>,
    parameters: List<GroovydocParameter>
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
//    list.addAll(this.typeParameters().toWildcardSignature())
    list.add(SignatureComponent("type", parent.simpleTypeName(), parent.qualifiedTypeName()))
    list.addAll(parameters.toParameterListSignature())

    return list
}