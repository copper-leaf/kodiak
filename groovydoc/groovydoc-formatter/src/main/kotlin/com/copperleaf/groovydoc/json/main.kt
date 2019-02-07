package com.copperleaf.groovydoc.json

import com.eden.orchid.api.options.annotations.Option
import java.io.File
import java.nio.file.Path

fun main(vararg args: String) {
    val formatter = GroovydocFormatter()
    formatter.init(args.toList().toTypedArray())
    try {
        formatter.runGroovydoc()
    }
    catch(e: Exception) {
        e.printStackTrace()
    }
}

class MainArgs {

    @Option
    lateinit var src: String

    @Option
    lateinit var cacheDir: String

    @Option
    lateinit var output: String

    val srcDirs: List<String> by lazy {
        src.split(File.pathSeparator)
    }

    val cachePath: Path by lazy {
        File(cacheDir).toPath()
    }

    val outputPath: Path by lazy {
        File(output).toPath()
    }

}
