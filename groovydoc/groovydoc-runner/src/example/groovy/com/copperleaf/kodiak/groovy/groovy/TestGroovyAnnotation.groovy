package com.copperleaf.kodiak.groovy.groovy

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface TestGroovyAnnotation {

    int anInt() default 0

}
