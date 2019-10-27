package com.copperleaf.kodiak;

class GroovyClassWithSuppressedMembers {

    GroovyClassWithSuppressedMembers() {
    }

    /**
     * @suppress
     */
    GroovyClassWithSuppressedMembers(int dontShowThisConstructor) {
        this();
    }
    GroovyClassWithSuppressedMembers(String showThisConstructor) {
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
