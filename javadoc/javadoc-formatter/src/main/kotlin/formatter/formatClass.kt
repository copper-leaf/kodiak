package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.java.models.JavaClass
import com.sun.javadoc.ClassDoc

fun ClassDoc.toClassDoc(deep: Boolean): JavaClass {
    val modifiers = listOf(this.modifiers())

    return JavaClass(
        this,
        this.containingPackage().name(),
        this.superclass()?.asCommentComponent(),
        this.interfaces()?.mapNotNull { it?.asCommentComponent() } ?: emptyList(),
        this.classKind,
        this.typeName(),
        this.qualifiedTypeName(),
        modifiers,
        this.getComment(),
        if (deep) this.constructors().filter { !it.isSuppressed() }.map { it.toConstructor() } else emptyList(),
        if (deep) this.methods().filter { !it.isSuppressed() }.map { it.toMethod() } else emptyList(),
        if (deep) this.fields().filter { !it.isSuppressed() }.map { it.toField() } else emptyList(),
        this.classSignature(
            modifiers
        ),

        if(deep && this.isEnum) this.enumConstants().map { it.toEnumConstant() } else emptyList()
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
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(CommentComponent(TEXT, "${this.classKind} "))
    list.add(this.asCommentComponent())
    list.addAll(this.typeParameters().toWildcardSignature())

    if (this.isInterface) {
        val interfaces = this.interfaces()
        if (interfaces.isNotEmpty()) {
            list.add(CommentComponent(TEXT, " extends "))
            interfaces.forEachIndexed { boundsIndex, type ->
                list.addAll(type.toTypeSignature())
                if (boundsIndex < interfaces.size - 1) {
                    list.add(CommentComponent(PUNCTUATION, ", "))
                }
            }
        }
    } else {
        val superclass = this.superclass()
        if (superclass != null) {
            list.add(CommentComponent(TEXT, " extends "))
            list.addAll(superclass.toTypeSignature())
        }

        val interfaces = this.interfaces()
        if (interfaces.isNotEmpty()) {
            list.add(CommentComponent(TEXT, " implements "))
            interfaces.forEachIndexed { boundsIndex, type ->
                list.addAll(type.toTypeSignature())
                if (boundsIndex < interfaces.size - 1) {
                    list.add(CommentComponent(PUNCTUATION, ", "))
                }
            }
        }
    }

    return list
}
