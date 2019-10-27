package com.copperleaf.kodiak.swift

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.swift.internal.models.SwiftAccessibility
import com.eden.orchid.api.cli.Cli
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.api.options.annotations.StringDefault
import java.io.File
import java.nio.file.Path

fun main(vararg args: String) {
    Clog.getInstance().addTagToBlacklist("FlagsParser")

    val mainArgs = Cli.parseArgsInto(MainArgs(), args)

    SourcekittenWrapper(mainArgs).use { sourceKitten ->
        sourceKitten.cacheSourceKittenBinary()
        sourceKitten.printSourceKittenVersion()

        SwiftdocFileParser(
            mainArgs,
            sourceKitten,
            SwiftdocRemapper(mainArgs)
        ).processAll()
    }
}

class MainArgs {

    @Option
    lateinit var src: String

    @Option
    lateinit var cacheDir: String

    @Option
    lateinit var output: String

    @JvmSuppressWildcards
    @Option @StringDefault("OPEN", "PUBLIC")
    lateinit var accessibility: List<String>

    val visibility: List<SwiftAccessibility> get() = accessibility.map { SwiftAccessibility.valueOf(it) }

    val cachePath: Path by lazy {
        File(cacheDir).toPath()
    }

    val srcDirs: List<String> by lazy {
        src.split(File.pathSeparator)
    }

    val srcPaths: List<Path> by lazy {
        srcDirs.map { File(it).toPath() }
    }

    val outputPath: Path by lazy {
        File(output).toPath()
    }

    val sourceKittenBinary: Path by lazy {
        cachePath.resolve("bin/sourcekitten")
    }
}

internal fun newFile(name: String) : File = File(name).apply {
    if (!parentFile.exists()) {
        parentFile.mkdirs()
    }
}
