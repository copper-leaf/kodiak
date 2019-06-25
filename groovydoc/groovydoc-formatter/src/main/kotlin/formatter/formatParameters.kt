package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.CommentComponent
import org.codehaus.groovy.groovydoc.GroovyParameter
import org.codehaus.groovy.groovydoc.GroovyTag
import org.codehaus.groovy.groovydoc.GroovyType
import com.copperleaf.kodiak.groovy.models.GroovyParameter as GroovyParameterDoc

fun formatParameters(params: Array<GroovyParameter>, tags: List<GroovyTag>): List<GroovyParameterDoc> {
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
        this.realType().toTypeSignature()
    )
}

fun List<GroovyParameterDoc>.toParameterListSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()
    list.add(CommentComponent("punctuation", "("))
    this.forEachIndexed { index, parameter ->
        list.addAll(parameter.signature)

        list.add(CommentComponent("name", " ${parameter.name}"))

        if (index < this.size - 1) {
            list.add(CommentComponent("punctuation", ", "))
        }
    }
    list.add(CommentComponent("punctuation", ")"))

    return list
}

fun GroovyType?.toTypeSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    if (this != null) {
        list.add(CommentComponent("typeName", this.real().simpleTypeName(), this.real().qualifiedTypeName()))

//    val wildcard = this.asWildcardType()
//    if(wildcard != null) {
//        val extendsTypes = wildcard.extendsBounds()
//        if(extendsTypes.isNotEmpty()) {
//            list.add(CommentComponent("name", " extends "))
//            extendsTypes.forEachIndexed { index, parameter ->
//                list.addAll(parameter.toTypeSignature())
//                if (index < extendsTypes.size - 1) {
//                    list.add(CommentComponent("punctuation", ", "))
//                }
//            }
//        }
//
//        val superTypes = wildcard.superBounds()
//        if(superTypes.isNotEmpty()) {
//            list.add(CommentComponent("name", " extends "))
//            superTypes.forEachIndexed { index, parameter ->
//                list.addAll(parameter.toTypeSignature())
//                if (index < superTypes.size - 1) {
//                    list.add(CommentComponent("punctuation", ", "))
//                }
//            }
//        }
//    }

//    if (this.asParameterizedType() != null) {
//        val typeArguments = this.asParameterizedType().typeArguments()
//        list.add(CommentComponent("punctuation", "<"))
//        typeArguments.forEachIndexed { index, parameter ->
//            list.addAll(parameter.toTypeSignature())
//            if (index < typeArguments.size - 1) {
//                list.add(CommentComponent("punctuation", ", "))
//            }
//        }
//        list.add(CommentComponent("punctuation", ">"))
//    }

    }

    return list
}

//fun Array<TypeVariable>.toWildcardSignature() : List<CommentComponent> {
//    val list = mutableListOf<CommentComponent>()
//
//    if(this.isNotEmpty()) {
//        list.add(CommentComponent("punctuation", "<"))
//        this.forEachIndexed { index, typeVariable ->
//            list.add(CommentComponent("name", typeVariable.simpleTypeName()))
//
//            val typeParamBounds = typeVariable.bounds()
//            if(typeParamBounds.isNotEmpty()) {
//                list.add(CommentComponent("name", " extends "))
//
//                typeParamBounds.forEachIndexed { boundsIndex, typeName ->
//                    list.addAll(typeName.toTypeSignature())
//                    if (boundsIndex < typeParamBounds.size - 1) {
//                        list.add(CommentComponent("punctuation", " & "))
//                    }
//                }
//            }
//
//            if (index < this.size - 1) {
//                list.add(CommentComponent("punctuation", ", "))
//            }
//        }
//        list.add(CommentComponent("punctuation", ">"))
//    }
//
//    return list
//}