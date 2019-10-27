package com.copperleaf.kodiak;

public class JavaClassWithSuppressedMembers {

    public JavaClassWithSuppressedMembers() {
    }

    /**
     * @suppress
     */
    public JavaClassWithSuppressedMembers(int dontShowThisConstructor) {
        this();
    }
    public JavaClassWithSuppressedMembers(String showThisConstructor) {
        this();
    }

    /**
     * @suppress
     */
    public int dontShowThisProperty = 0;
    public String showThisProperty = "";

    /**
     * @suppress
     */
    public int dontShowThisFunction() { return 0; }
    public String showThisFunction() { return ""; }
}
