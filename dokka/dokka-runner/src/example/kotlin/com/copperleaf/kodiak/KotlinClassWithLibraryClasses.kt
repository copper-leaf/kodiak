package com.copperleaf.kodiak

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.impl.MinLengthSpek
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor

class KotlinClassWithLibraryClasses {

    fun testMinLengthFail(): Spek<String, Boolean> {
        val input = "asdf"
        val visitor = EmptyVisitor
        return MinLengthSpek(6)
    }

    fun methodReturnsClassNeverDefinedAtAll(): ClassNeverDefinedAtAll = ClassNeverDefinedAtAll()

}
