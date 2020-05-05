package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.groovy.models.GroovyEnumConstant
import org.codehaus.groovy.groovydoc.GroovyFieldDoc

fun GroovyFieldDoc.toEnumConstant(): GroovyEnumConstant {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    return GroovyEnumConstant(
        this,
        this.name(),
        this.name(),
        modifiers,
        this.getComment(),
        this.enumConstantSignature()
    )
}

fun GroovyFieldDoc.enumConstantSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.add(RichTextComponent(TEXT, this.name()))

    return list
}
