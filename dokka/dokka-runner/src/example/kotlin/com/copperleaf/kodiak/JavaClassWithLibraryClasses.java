package com.copperleaf.kodiak;

import com.copperleaf.trellis.api.Spek;
import com.copperleaf.trellis.impl.MinLengthSpek;
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor;

public class JavaClassWithLibraryClasses {

    public Spek<String, Boolean> testMinLengthFail() {
        EmptyVisitor visitor = EmptyVisitor.INSTANCE;
        return new MinLengthSpek(6);
    }

    public ClassNeverDefinedAtAll methodReturnsClassNeverDefinedAtAll() {
        return new ClassNeverDefinedAtAll();
    }
}
