package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocField
import com.copperleaf.groovydoc.json.models.SignatureComponent
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyFieldDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyFieldDoc.toField(parent: GroovyClassDoc): GroovydocField {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    return GroovydocField(
            this,
            this.name(),
            this.name(),
            this.commentText().trim(),
            modifiers,
            this.type().real().simpleTypeName(),
            this.type().real().qualifiedTypeName(),
            this.fieldSignature(
                    modifiers,
                    this.type()
            )
    )
}

fun GroovyFieldDoc.fieldSignature(
        modifiers: List<String>,
        type: GroovyType
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.addAll(type.toTypeSignature())
    list.add(SignatureComponent("name", " ${this.name()}", ""))

    return list
}