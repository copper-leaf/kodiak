package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovyConstructor
import com.copperleaf.groovydoc.json.models.GroovyParameter
import com.copperleaf.json.common.CommentComponent
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyConstructorDoc

fun GroovyConstructorDoc.toConstructor(parent: GroovyClassDoc): GroovyConstructor {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    val parameters = formatParameters(this.parameters(), this.findCommentTags().filter { it.name() == "parameters" })
    return GroovyConstructor(
        this,
        parent.simpleTypeName(),
        parent.qualifiedTypeName(),
        modifiers,
        this.getComment(),
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
    parameters: List<GroovyParameter>
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
//    list.addAll(this.typeParameters().toWildcardSignature())
    list.add(CommentComponent("typeName", parent.simpleTypeName(), parent.qualifiedTypeName()))
    list.addAll(parameters.toParameterListSignature())

    return list
}