package com.copperleaf.kss.json

import com.caseyjbrooks.clog.Clog
import com.eden.orchid.api.cli.Cli
import com.eden.orchid.api.options.annotations.Option
import java.io.File
import java.nio.file.Path

const val FILE_ENCODING = "UTF-8"

fun main(vararg args: String) {
    Clog.getInstance().addTagToBlacklist("FlagsParser")
    val mainArgs = Cli.parseArgsInto(MainArgs(), args)

    KssJsonFormatter(mainArgs.srcPaths, mainArgs.outputPath).execute()
}

class MainArgs {

    @Option
    lateinit var src: String

    @Option
    lateinit var output: String

    val srcDirs: List<String> by lazy {
        src.split(File.pathSeparator)
    }

    val srcPaths: List<Path> by lazy {
        srcDirs.map { File(it).toPath() }
    }

    val outputPath: Path by lazy {
        File(output).toPath()
    }

}
