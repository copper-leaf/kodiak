package com.copperleaf.kss.json

import com.caseyjbrooks.clog.Clog
import java.nio.file.Path

class KssJsonFormatter(
    private val dirs: List<Path>,
    private val destDir: Path
) {

    private var extensions = listOf("css", "scss", "less")

    fun execute() {
        Clog.v("Running KSS json formatter")
    }

}
