package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import org.codehaus.groovy.groovydoc.GroovyParameter
import org.codehaus.groovy.groovydoc.GroovyTag
import org.codehaus.groovy.groovydoc.GroovyType
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyParameter
import com.copperleaf.kodiak.groovy.models.GroovyParameter as GroovyParameterDoc

fun formatParameters(
    params: Array<GroovyParameter>,
    tags: List<GroovyTag>
): List<GroovyParameterDoc> {
    return params.map { param ->
        param.toParameter(tags.find { tag -> tag.param() == param.name() })
    }
}

fun GroovyParameter.toParameter(tag: GroovyTag?): GroovyParameterDoc {
    return GroovyParameterDoc(
        this,
        this.name(),
        this.name(),
        emptyList(),
        tag.getComment(),
        this.realType().simpleTypeName(),
        this.realType().qualifiedTypeName(),
        this.parameterSignature()
    )
}

fun List<GroovyParameterDoc>.toParameterListSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()
    list.add(RichTextComponent(PUNCTUATION, "("))
    this.forEachIndexed { index, parameter ->
        list.addAll(parameter.signature)

        if (index < this.size - 1) {
            list.add(RichTextComponent(PUNCTUATION, ", "))
        }
    }
    list.add(RichTextComponent(PUNCTUATION, ")"))

    return list
}

fun GroovyType?.toTypeSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    if (this != null) {
        list.add(RichTextComponent(TYPE_NAME, this.real().simpleTypeName(), this.real().qualifiedTypeName()))
    }

    return list
}

fun GroovyParameter.parameterSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.addAll(this.realType().toTypeSignature())

    val dimension = this.dimension()
    if (dimension.isNotBlank()) {
        list.add(RichTextComponent(TEXT, dimension, ""))
    }

    list.add(
        RichTextComponent(
            TEXT,
            " ${this.name()}",
            ""
        )
    )

    return list
}

private fun GroovyParameter.dimension(): String {
    val thisTypeName: String
    if (this is SimpleGroovyParameter) {
        val currentType = this.type()
        this.setType(null)
        thisTypeName = this.typeName()
        this.setType(currentType)
    } else {
        thisTypeName = ""
    }

    if (thisTypeName.contains("[]")) {
        val trimmed = thisTypeName.replaceBefore('[', "")

        if (this is SimpleGroovyParameter && this.vararg()) {
            val vararged = trimmed.replaceAfterLast("[]", "...")
            return vararged
        } else {
            return trimmed
        }
    } else {
        return ""
    }
}
