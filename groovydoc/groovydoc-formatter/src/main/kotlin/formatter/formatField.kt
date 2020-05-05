package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.groovy.models.GroovyField
import org.codehaus.groovy.groovydoc.GroovyFieldDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyFieldDoc.toField(): GroovyField {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    return GroovyField(
        this,
        this.name(),
        this.name(),
        modifiers,
        this.getComment(),
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
): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.addAll(type.toTypeSignature())
    list.add(RichTextComponent(TEXT, " ${this.name()}"))

    return list
}
