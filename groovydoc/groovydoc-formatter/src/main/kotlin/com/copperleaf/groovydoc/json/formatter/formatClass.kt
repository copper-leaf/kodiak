package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocClassDoc
import com.copperleaf.groovydoc.json.models.SignatureComponent
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyDoc

fun GroovyClassDoc.toClassDoc(deep: Boolean = true): GroovydocClassDoc {
    val modifiers = listOf(this.modifiers()).filterNotNull()

    return GroovydocClassDoc(
        this,
        this.containingPackage().nameWithDots(),
        modifiers,
        this.classKind,
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        this.findCommentText(),
        if(deep) this.constructors().map { it.toConstructor(this) } else emptyList(),
        if(deep) this.methods().map { it.toMethod(this) } else emptyList(),
        if(deep) this.fields().map { it.toField(this) } else emptyList(),
        this.classSignature(
            modifiers
        )
    )
}

val GroovyClassDoc.classKind: String
    get() {
        return when {
            isInterface -> "interface"
            isAnnotationType -> "@interface"
            isEnum -> "enum"
            isExceptionClass() -> "exception"
            this is SimpleGroovyDoc && isTrait -> "trait"
            else -> "class"
        }
    }

fun GroovyClassDoc.classSignature(
    modifiers: List<String>
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(SignatureComponent("name", "${this.classKind} ", ""))
    list.add(SignatureComponent("type", "" + this.simpleTypeName(), "" + this.qualifiedTypeName()))
//    list.addAll(this.typeParameters().toWildcardSignature())

    if(this.isInterface) {
        val interfaces = this.interfaces()
        if(interfaces.isNotEmpty()) {
            list.add(SignatureComponent("name", " extends ", ""))
            interfaces.forEachIndexed { boundsIndex, type ->
                list.addAll(type.toTypeSignature())
                if (boundsIndex < interfaces.size - 1) {
                    list.add(SignatureComponent("punctuation", ", ", ""))
                }
            }
        }
    }
    else {
        val superclass = this.superclass()
        if(superclass != null) {
            list.add(SignatureComponent("name", " extends ", ""))
            list.addAll(superclass.toTypeSignature())
        }

        val interfaces = this.interfaces()
        if(interfaces.isNotEmpty()) {
            list.add(SignatureComponent("name", " implements ", ""))
            interfaces.forEachIndexed { boundsIndex, type ->
                list.addAll(type.toTypeSignature())
                if (boundsIndex < interfaces.size - 1) {
                    list.add(SignatureComponent("punctuation", ", ", ""))
                }
            }
        }
    }

    return list
}