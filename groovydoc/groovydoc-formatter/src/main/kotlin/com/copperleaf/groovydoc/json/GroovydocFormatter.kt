package com.copperleaf.groovydoc.json

import com.caseyjbrooks.clog.Clog
import com.eden.orchid.api.cli.Cli
import groovy.lang.GroovySystem

class GroovydocFormatter {

    lateinit var mainArgs: MainArgs

    fun init(args: Array<String>) {
        parseCliFlags(args)
        printGroovyVersion()
    }

    private fun parseCliFlags(args: Array<String>) {
        Clog.getInstance().setMinPriority(Clog.Priority.DEBUG)
        mainArgs = Cli.parseArgsInto(MainArgs(), args)
    }

    private fun printGroovyVersion() {
        Clog.i("Groovy version: {}", GroovySystem.getVersion())
    }

    fun runGroovydoc() {
        GroovydocJsonFormatter(mainArgs.srcPaths, mainArgs.outputPath).execute()
    }

}
