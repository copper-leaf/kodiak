package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaParameter
import com.copperleaf.javadoc.json.models.SignatureComponent
import com.sun.javadoc.ParamTag
import com.sun.javadoc.Parameter
import com.sun.javadoc.Tag
import com.sun.javadoc.Type
import com.sun.javadoc.TypeVariable

fun formatParameters(params: Array<Parameter>, tags: Array<ParamTag>): List<JavaParameter> {
    return params.map { param ->
        param.toParameter(tags.find { tag -> tag.parameterName() == param.name() })
    }
}

fun Parameter.toParameter(tag: ParamTag?): JavaParameter {
    return JavaParameter(
            this,
            this.name(),
            this.name(),
            if (tag != null) tag.parameterComment() else "",
            if (tag != null) arrayOf<Tag>(tag).asCommentTags() else emptyList(),
            if (tag != null) arrayOf<Tag>(tag).asCommentTagsMap() else emptyMap(),
            this.type().simpleTypeName(),
            this.type().qualifiedTypeName(),
            this.type().toTypeSignature()
    )
}

fun List<JavaParameter>.toParameterListSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()
    list.add(SignatureComponent("punctuation", "(", ""))
    this.forEachIndexed { index, parameter ->
        list.addAll(parameter.signature)

        list.add(SignatureComponent("name", " ${parameter.name}", ""))

        if (index < this.size - 1) {
            list.add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    list.add(SignatureComponent("punctuation", ")", ""))

    return list
}

fun Type.toTypeSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.add(SignatureComponent("type", this.simpleTypeName(), this.qualifiedTypeName()))

    val wildcard = this.asWildcardType()
    if(wildcard != null) {
        val extendsTypes = wildcard.extendsBounds()
        if(extendsTypes.isNotEmpty()) {
            list.add(SignatureComponent("name", " extends ", ""))
            extendsTypes.forEachIndexed { index, parameter ->
                list.addAll(parameter.toTypeSignature())
                if (index < extendsTypes.size - 1) {
                    list.add(SignatureComponent("punctuation", ", ", ""))
                }
            }
        }

        val superTypes = wildcard.superBounds()
        if(superTypes.isNotEmpty()) {
            list.add(SignatureComponent("name", " extends ", ""))
            superTypes.forEachIndexed { index, parameter ->
                list.addAll(parameter.toTypeSignature())
                if (index < superTypes.size - 1) {
                    list.add(SignatureComponent("punctuation", ", ", ""))
                }
            }
        }
    }

    if (this.asParameterizedType() != null) {
        val typeArguments = this.asParameterizedType().typeArguments()
        list.add(SignatureComponent("punctuation", "<", ""))
        typeArguments.forEachIndexed { index, parameter ->
            list.addAll(parameter.toTypeSignature())
            if (index < typeArguments.size - 1) {
                list.add(SignatureComponent("punctuation", ", ", ""))
            }
        }
        list.add(SignatureComponent("punctuation", ">", ""))
    }

    return list
}

fun Array<TypeVariable>.toWildcardSignature() : List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    if(this.isNotEmpty()) {
        list.add(SignatureComponent("punctuation", "<", ""))
        this.forEachIndexed { index, typeVariable ->
            list.add(SignatureComponent("name", typeVariable.simpleTypeName(), ""))

            val typeParamBounds = typeVariable.bounds()
            if(typeParamBounds.isNotEmpty()) {
                list.add(SignatureComponent("name", " extends ", ""))

                typeParamBounds.forEachIndexed { boundsIndex, type ->
                    list.addAll(type.toTypeSignature())
                    if (boundsIndex < typeParamBounds.size - 1) {
                        list.add(SignatureComponent("punctuation", " & ", ""))
                    }
                }
            }

            if (index < this.size - 1) {
                list.add(SignatureComponent("punctuation", ", ", ""))
            }
        }
        list.add(SignatureComponent("punctuation", ">", ""))
    }

    return list
}