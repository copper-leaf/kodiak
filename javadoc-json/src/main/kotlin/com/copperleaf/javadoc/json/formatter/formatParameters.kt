package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaParameter
import com.sun.javadoc.ParamTag
import com.sun.javadoc.Parameter
import com.sun.javadoc.Tag

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
            if(tag != null) tag.parameterComment() else "",
            if(tag != null) arrayOf<Tag>(tag).asCommentTags() else emptyList(),
            if(tag != null) arrayOf<Tag>(tag).asCommentTagsMap() else emptyMap(),
            this.type().simpleTypeName(),
            this.type().qualifiedTypeName(),
            emptyList()
    )
}