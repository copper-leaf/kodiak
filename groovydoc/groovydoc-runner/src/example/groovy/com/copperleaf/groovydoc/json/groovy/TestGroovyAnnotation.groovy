package com.copperleaf.groovydoc.json.groovy

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface TestGroovyAnnotation {

    int anInt() default 0

}
