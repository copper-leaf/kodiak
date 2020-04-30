package com.copperleaf.kodiak

class GroovyClassWithSuperclassAndInterfaces extends GroovyClass implements GroovyInterface, Runnable {

    /**
     * This class _freaking awesome_ constructor links to {@link GroovyInterface}, **yo**!
     *
     * @param s1 This class _freaking awesome_ param links to {@link GroovyInterface}, **yo**!
     */
    GroovyClassWithSuperclassAndInterfaces(String s1) {
        super(s1)
    }

    @Override
    void doThing() {

    }

    @Override
    void run() {

    }
}
