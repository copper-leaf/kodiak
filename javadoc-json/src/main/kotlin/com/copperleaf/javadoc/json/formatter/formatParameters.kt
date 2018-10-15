package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaParameter
import com.copperleaf.javadoc.json.models.SignatureComponent
import com.sun.javadoc.ParamTag
import com.sun.javadoc.Parameter
import com.sun.javadoc.Tag
import com.sun.javadoc.Type

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

