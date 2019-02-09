package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocParameter
import com.copperleaf.groovydoc.json.models.SignatureComponent
import org.codehaus.groovy.groovydoc.GroovyParameter
import org.codehaus.groovy.groovydoc.GroovyType

fun formatParameters(params: Array<GroovyParameter>): List<GroovydocParameter> {
    return params.map { param ->
        param.toParameter()
    }
}

fun GroovyParameter.toParameter(): GroovydocParameter {
    return GroovydocParameter(
            this,
            this.name(),
            this.name(),
            "",
            this.realType().simpleTypeName(),
            this.realType().qualifiedTypeName(),
            this.realType().toTypeSignature()
    )
}

fun List<GroovydocParameter>.toParameterListSignature(): List<SignatureComponent> {
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

fun GroovyType?.toTypeSignature(): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    if(this != null) {
        list.add(SignatureComponent("type", this.real().simpleTypeName(), this.real().qualifiedTypeName()))

//    val wildcard = this.asWildcardType()
//    if(wildcard != null) {
//        val extendsTypes = wildcard.extendsBounds()
//        if(extendsTypes.isNotEmpty()) {
//            list.add(SignatureComponent("name", " extends ", ""))
//            extendsTypes.forEachIndexed { index, parameter ->
//                list.addAll(parameter.toTypeSignature())
//                if (index < extendsTypes.size - 1) {
//                    list.add(SignatureComponent("punctuation", ", ", ""))
//                }
//            }
//        }
//
//        val superTypes = wildcard.superBounds()
//        if(superTypes.isNotEmpty()) {
//            list.add(SignatureComponent("name", " extends ", ""))
//            superTypes.forEachIndexed { index, parameter ->
//                list.addAll(parameter.toTypeSignature())
//                if (index < superTypes.size - 1) {
//                    list.add(SignatureComponent("punctuation", ", ", ""))
//                }
//            }
//        }
//    }

//    if (this.asParameterizedType() != null) {
//        val typeArguments = this.asParameterizedType().typeArguments()
//        list.add(SignatureComponent("punctuation", "<", ""))
//        typeArguments.forEachIndexed { index, parameter ->
//            list.addAll(parameter.toTypeSignature())
//            if (index < typeArguments.size - 1) {
//                list.add(SignatureComponent("punctuation", ", ", ""))
//            }
//        }
//        list.add(SignatureComponent("punctuation", ">", ""))
//    }

    }

    return list
}

//fun Array<TypeVariable>.toWildcardSignature() : List<SignatureComponent> {
//    val list = mutableListOf<SignatureComponent>()
//
//    if(this.isNotEmpty()) {
//        list.add(SignatureComponent("punctuation", "<", ""))
//        this.forEachIndexed { index, typeVariable ->
//            list.add(SignatureComponent("name", typeVariable.simpleTypeName(), ""))
//
//            val typeParamBounds = typeVariable.bounds()
//            if(typeParamBounds.isNotEmpty()) {
//                list.add(SignatureComponent("name", " extends ", ""))
//
//                typeParamBounds.forEachIndexed { boundsIndex, type ->
//                    list.addAll(type.toTypeSignature())
//                    if (boundsIndex < typeParamBounds.size - 1) {
//                        list.add(SignatureComponent("punctuation", " & ", ""))
//                    }
//                }
//            }
//
//            if (index < this.size - 1) {
//                list.add(SignatureComponent("punctuation", ", ", ""))
//            }
//        }
//        list.add(SignatureComponent("punctuation", ">", ""))
//    }
//
//    return list
//}