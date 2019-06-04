package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocClassDoc
import com.copperleaf.json.common.CommentComponent
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
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(CommentComponent("name", "${this.classKind} "))
    list.add(CommentComponent("type", "" + this.simpleTypeName(), "" + this.qualifiedTypeName()))
//    list.addAll(this.typeParameters().toWildcardSignature())

    if(this.isInterface) {
        val interfaces = this.interfaces()
        if(interfaces.isNotEmpty()) {
            list.add(CommentComponent("name", " extends "))
            interfaces.forEachIndexed { boundsIndex, type ->
                list.addAll(type.toTypeSignature())
                if (boundsIndex < interfaces.size - 1) {
                    list.add(CommentComponent("punctuation", ", "))
                }
            }
        }
    }
    else {
        val superclass = this.superclass()
        if(superclass != null) {
            list.add(CommentComponent("name", " extends "))
            list.addAll(superclass.toTypeSignature())
        }

        val interfaces = this.interfaces()
        if(interfaces.isNotEmpty()) {
            list.add(CommentComponent("name", " implements "))
            interfaces.forEachIndexed { boundsIndex, type ->
                list.addAll(type.toTypeSignature())
                if (boundsIndex < interfaces.size - 1) {
                    list.add(CommentComponent("punctuation", ", "))
                }
            }
        }
    }

    return list
}