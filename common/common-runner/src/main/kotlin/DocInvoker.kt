package com.copperleaf.json.common

import java.io.InputStream
import java.nio.file.Path
import java.util.Collections.emptyList

interface DocInvoker<T : ModuleDoc> {
    fun getModuleDoc(
        sourceDirs: List<Path>,
        destinationDir: Path,
        cliArgs: List<String> = emptyList(),
        callback: (InputStream) -> Runnable
    ): T?

    fun loadCachedModuleDoc(destinationDir: Path): T?
}
