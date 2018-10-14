package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaClassDoc
import com.sun.javadoc.ClassDoc

fun ClassDoc.toClassDoc(deep: Boolean = false): JavaClassDoc {
    return JavaClassDoc(
            this,
            this.containingPackage().name(),
            this.classKind,
            this.typeName(),
            this.qualifiedTypeName(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
            if(deep) this.constructors().map { it.toConstructor() } else emptyList(),
            if(deep) this.methods().map { it.toMethod() } else emptyList(),
            if(deep) this.fields().map { it.toField() } else emptyList()
    )
}

val ClassDoc.classKind: String
    get() {
        return when {
            isInterface -> "interface"
            isAnnotationType -> "annotation"
            isException -> "exception"
            isOrdinaryClass -> "class"
            else -> throw IllegalArgumentException("Class kind not found")
        }
    }