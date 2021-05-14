package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.java.models.JavaParameter
import com.sun.javadoc.ParamTag
import com.sun.javadoc.Parameter
import com.sun.javadoc.Type
import com.sun.javadoc.TypeVariable

fun formatParameters(
    params: Array<Parameter>,
    tags: Array<ParamTag>,
    lastParamIsVarArgs: Boolean
): List<JavaParameter> {
    val paramWithVararg = if (lastParamIsVarArgs) params.lastOrNull { it.type().dimension().isNotBlank() } else null

    return params.map { param ->
        param.toParameter(
            tags.find { tag -> tag.parameterName() == param.name() },
            paramWithVararg != null && param === paramWithVararg
        )
    }
}

fun Parameter.toParameter(tag: ParamTag?, isVarArg: Boolean): JavaParameter {
    return JavaParameter(
        this,
        this.name(),
        this.name(),
        emptyList(),
        tag.getComment(this.name()),
        this.type().simpleTypeName(),
        this.type().qualifiedTypeName(),
        this.parameterSignature(isVarArg)
    )
}

fun List<JavaParameter>.toParameterListSignature(): List<RichTextComponent> {
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

fun Type.toTypeSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.add(RichTextComponent(TYPE_NAME, this.simpleTypeName(), this.qualifiedTypeName()))

    val wildcard = this.asWildcardType()
    if (wildcard != null) {
        val extendsTypes = wildcard.extendsBounds()
        if (extendsTypes.isNotEmpty()) {
            list.add(RichTextComponent(TEXT, " extends "))
            extendsTypes.forEachIndexed { index, parameter ->
                list.addAll(parameter.toTypeSignature())
                if (index < extendsTypes.size - 1) {
                    list.add(RichTextComponent(PUNCTUATION, ", "))
                }
            }
        }

        val superTypes = wildcard.superBounds()
        if (superTypes.isNotEmpty()) {
            list.add(RichTextComponent(TEXT, " extends "))
            superTypes.forEachIndexed { index, parameter ->
                list.addAll(parameter.toTypeSignature())
                if (index < superTypes.size - 1) {
                    list.add(RichTextComponent(PUNCTUATION, ", "))
                }
            }
        }
    }

    if (this.asParameterizedType() != null) {
        val typeArguments = this.asParameterizedType().typeArguments()
        list.add(RichTextComponent(PUNCTUATION, "<"))
        typeArguments.forEachIndexed { index, parameter ->
            list.addAll(parameter.toTypeSignature())
            if (index < typeArguments.size - 1) {
                list.add(RichTextComponent(PUNCTUATION, ", "))
            }
        }
        list.add(RichTextComponent(PUNCTUATION, ">"))
    }

    return list
}

fun Parameter.parameterSignature(isVarArg: Boolean): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    list.addAll(this.type().toTypeSignature())

    val parameterDimension = this.type().dimension()
    if (parameterDimension.isNotBlank()) {
        if (isVarArg) {
            list.add(RichTextComponent(TEXT, parameterDimension.removeSuffix("[]") + "...", ""))
        } else {
            list.add(RichTextComponent(TEXT, parameterDimension, ""))
        }
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

fun Array<TypeVariable>.toWildcardSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    if (this.isNotEmpty()) {
        list.add(RichTextComponent(PUNCTUATION, "<"))
        this.forEachIndexed { index, typeVariable ->
            list.add(RichTextComponent(TEXT, typeVariable.simpleTypeName()))

            val typeParamBounds = typeVariable.bounds()
            if (typeParamBounds.isNotEmpty()) {
                list.add(RichTextComponent(TEXT, " extends "))

                typeParamBounds.forEachIndexed { boundsIndex, type ->
                    list.addAll(type.toTypeSignature())
                    if (boundsIndex < typeParamBounds.size - 1) {
                        list.add(RichTextComponent(PUNCTUATION, " & "))
                    }
                }
            }

            if (index < this.size - 1) {
                list.add(RichTextComponent(PUNCTUATION, ", "))
            }
        }
        list.add(RichTextComponent(PUNCTUATION, ">"))
    }

    return list
}
