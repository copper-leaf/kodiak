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
//        CustomGroovydoc().let {
//            val project = Project()
//            project.init()
//
//            it.setSourcepath(
//                mainArgs
//                    .srcDirs
//                    .map { dir -> Path(project, dir) }
//                    .reduce { acc, path -> acc.append(path); acc }
//            )
//            it.setDestdir(mainArgs.outputPath.toFile())
//            it.setPackagenames("**.*")
//            it.setAccess("public")
//            it.setUse(true)
//
//            it.execute()
//        }
//        val buildFile = File("build.xml")
//        val p = Project()
//        p.setUserProperty("ant.file", buildFile.getAbsolutePath())
//        p.init()
//        val helper = ProjectHelper.getProjectHelper()
//        p.addReference("ant.projectHelper", helper)
//        helper.parse(p, buildFile)
//        p.executeTarget(p.defaultTarget)
    }

}
