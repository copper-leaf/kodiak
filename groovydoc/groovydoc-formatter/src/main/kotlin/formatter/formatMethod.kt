package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.ElementType
import com.copperleaf.kodiak.groovy.models.GroovyMethod
import com.copperleaf.kodiak.groovy.models.GroovyParameter
import com.copperleaf.kodiak.groovy.models.GroovyReturnType
import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyMethodDoc.toMethod(): GroovyMethod {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    val parameters = formatParameters(this.parameters(), this.findCommentTags().filter { it.name() == "parameters" })
    val returnType = this.returnType().real().toReturnType(this)
    return GroovyMethod(
        this,
        this.name(),
        this.name(),
        modifiers,
        this.getComment(),
        parameters,
        returnType,
        this.methodSignature(
            modifiers,
            parameters,
            returnType
        )
    )
}

fun GroovyType.toReturnType(parent: GroovyMethodDoc): GroovyReturnType {
    val returnTag = parent.findCommentTags().firstOrNull { it.name() == "returns" }
    return GroovyReturnType(
        this,
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        emptyList(),
        returnTag.getComment(),
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        this.toTypeSignature()
    )
}

fun GroovyMethodDoc.methodSignature(
    modifiers: List<String>,
    parameters: List<GroovyParameter>,
    returnType: ElementType
): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.addAll(modifiers.toModifierListSignature())
//    list.addAll(this.typeParameters().toWildcardSignature())
    list.addAll(returnType.signature)
    list.add(RichTextComponent(TEXT, " ${this.name()}"))
    list.addAll(parameters.toParameterListSignature())

    return list
}
