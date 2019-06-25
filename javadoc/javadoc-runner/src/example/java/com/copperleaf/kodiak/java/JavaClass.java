package com.copperleaf.kodiak.java;

import com.copperleaf.kodiak.json.test.java.JavaMarkdown;
import com.copperleaf.kodiak.json.test.java.camelCasePackage.JavaInterface2;

import java.util.List;

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

    /**
     * Method comments
     *
     * @param param1 param1 docs
     * @param param2 param2 docs
     * @return return value
     */
    public List<String> classMethodWithDefinedTypeParams(List<String> param1, List<String> param2) {
        return null;
    }

    /**
     * Method comments
     *
     * @param param1 param1 docs
     * @param param2 param2 docs
     * @param <T> typeName param
     * @return return value
     */
    public <T> List<T> classMethodWithGenericTypeParams(List<T> param1, List<T> param2) {
        return null;
    }

    /**
     * Method comments
     *
     * @param param1 param1 docs
     * @param param2 param2 docs
     * @param <T> typeName param
     * @return return value
     */
    public <T extends JavaInterface & JavaInterface2> List<T> classMethodWithGenericExtendsTypeParams(List<T> param1, List<T> param2) {
        return null;
    }

    /**
     * Method comments
     *
     * @param param1 param1 docs
     * @param param2 param2 docs
     * @return return value
     */
    public List<? extends JavaInterface> classMethodWithWildcardExtendsParams(List<? extends JavaInterface> param1, List<? extends List<? extends JavaInterface>> param2) {
        return null;
    }

    /**
     * Method comments
     *
     * @param param1 param1 docs
     * @param param2 param2 docs
     * @return return value
     */
    public List<? super JavaInterface> classMethodWithWildcardSuperParams(List<? super JavaInterface> param1, List<? super List<? super JavaInterface>> param2) {
        return null;
    }

}