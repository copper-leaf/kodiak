package com.copperleaf.kodiak.java;

/**
 * This is a Kotlin interface
 */
public interface JavaInterface {

    /**
     * This is a method defined in the interface body
     */
    String interfaceMethod();

    /**
     * This is a property defined in the interface body
     */
    String getInterfaceProperty();

    /**
     * This is a property defined in the interface body
     */
    void setInterfaceProperty(String value);

    /**
     * This is a method defined in the interface body, which has parameters
     *
     * @param param This is the param for the method
     * @return This is the returned string value
     */
    String interfaceMethodWithParams(String param);

}