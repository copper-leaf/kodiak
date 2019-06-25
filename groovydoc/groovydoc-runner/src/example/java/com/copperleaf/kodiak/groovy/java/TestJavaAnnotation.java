package com.copperleaf.kodiak.groovy.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestJavaAnnotation {

    int anInt() default 0;

}
