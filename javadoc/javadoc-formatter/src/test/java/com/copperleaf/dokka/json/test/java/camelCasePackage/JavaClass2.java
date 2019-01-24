package com.copperleaf.dokka.json.test.java.camelCasePackage;

import com.copperleaf.dokka.json.test.java.JavaClass;
import com.copperleaf.dokka.json.test.java.JavaInterface;

/**
 * This is a Java class
 */
public abstract class JavaClass2<T> extends JavaClass implements JavaInterface, JavaInterface2 {

    /**
     * This is a property defined in the primary constructor
     */
    public final String constructorProperty;

    /**
     * This is a property defined in the class body
     */
    public String classProperty;

    public JavaClass2(String constructorProperty) {
        super(constructorProperty);
        this.constructorProperty = constructorProperty;
    }

    /**
     * This is a method defined in the class body
     */
    public String classMethod() {
        return null;
    }

    /**
     * This is a method defined in the class body, which has parameters
     *
     * @param param This is the param for the method
     * @return This is the returned string value
     */
    public String classMethodWithParams(String param) {
        return null;
    }

    @Override
    public String interfaceMethod() {
        return null;
    }

    @Override
    public String getInterfaceProperty() {
        return null;
    }

    @Override
    public void setInterfaceProperty(String value) {

    }

    @Override
    public String interfaceMethodWithParams(String param) {
        return null;
    }
}