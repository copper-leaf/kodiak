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

    /**
     * This is a property defined in the class body
     */
    public JavaInterface classInternalProperty;

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
     * This is a method defined in the class body which returns nothing
     */
    public void voidMethod() {

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

    /**
     * This is a method defined in the class body, which has parameters
     *
     * @param param1 This is the param1 for the method
     * @param param2 This is the param2 for the method
     * @return This is the returned string value
     */
    public JavaInterface classMethodWithInternalarams(JavaInterface param1, JavaMarkdown param2) {
        return null;
    }
}