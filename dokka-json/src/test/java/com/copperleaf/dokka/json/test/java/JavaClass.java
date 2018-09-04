package com.copperleaf.dokka.json.test.java;

/**
 * This is a Java class
 */
public class JavaClass {

    /**
     * This is a property defined in the primary constructor
     */
    public final String constructorProperty;

    /**
     * This is a property defined in the class body
     */
    public String classProperty;

    public JavaClass(String constructorProperty) {
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
}