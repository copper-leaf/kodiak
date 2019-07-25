package com.copperleaf.kodiak

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
public @interface GroovyAnnotation {

    int anInt() default 0

}
