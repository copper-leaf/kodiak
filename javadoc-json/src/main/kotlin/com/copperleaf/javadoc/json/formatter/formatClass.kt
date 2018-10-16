package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaClassDoc
import com.copperleaf.javadoc.json.models.SignatureComponent
import com.sun.javadoc.ClassDoc

fun ClassDoc.toClassDoc(deep: Boolean = false): JavaClassDoc {
    val modifiers = listOf(this.modifiers())

    return JavaClassDoc(
            this,
            this.containingPackage().name(),
            modifiers,
            this.classKind,
            this.typeName(),
            this.qualifiedTypeName(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
            if(deep) this.constructors().map { it.toConstructor() } else emptyList(),
            if(deep) this.methods().map { it.toMethod() } else emptyList(),
            if(deep) this.fields().map { it.toField() } else emptyList(),
            this.classSignature(
                    modifiers
            )
    )
}

val ClassDoc.classKind: String
    get() {
        return when {
            isInterface -> "interface"
            isAnnotationType -> "@interface"
            isEnum -> "enum"
            isException -> "exception"
            isOrdinaryClass -> "class"
            else -> throw IllegalArgumentException("Class kind not found")
        }
    }

fun ClassDoc.classSignature(
        modifiers: List<String>
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(SignatureComponent("name", "${this.classKind} ", ""))
    list.add(SignatureComponent("type", this.name(), this.qualifiedName()))
    list.addAll(this.typeParameters().toWildcardSignature())

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