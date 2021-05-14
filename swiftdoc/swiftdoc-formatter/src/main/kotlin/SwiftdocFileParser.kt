package com.copperleaf.kodiak.swift

import clog.Clog
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.swift.internal.models.SourceKittenParseResult
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path

class SwiftdocFileParser(
    val mainArgs: MainArgs,
    val sourceKittenWrapper: SourcekittenWrapper,
    val remapper: SwiftdocRemapper,
    val jsonModule: Json,
) {

    fun processAll() {
        processDirectories()
    }

    private fun processDirectories() {
        mainArgs.srcPaths.forEach { processDirectory(it) }
    }

    private fun processDirectory(dir: Path) {
        dir.toFile().walkTopDown()
            .filter { it.isFile }
            .filter { it.exists() }
            .filter { it.extension == "swift" }
            .forEach { processFile(dir, it) }
    }

    private fun processFile(dir: Path, file: File): Map<Class<DocElement>, List<DocElement>>? {
        val doc = sourceKittenWrapper.proc("doc", "--single-file", file.absolutePath)

        val relativeFile = file.relativeTo(dir.toFile()).path.removeSuffix(".swift")
        val outputCacheDir = mainArgs.outputPath.toFile().absolutePath + "/.cache/" + file.relativeTo(dir.toFile()).path
        val singleFile = newFile("$outputCacheDir/single-file.json")
        val structureFile = newFile("$outputCacheDir/structure.json")

        singleFile.writeText(doc)

        val sourceKittenDocs = SourceKittenParseResult.fromJson(jsonModule, doc)

        sourceKittenDocs.files.forEach { (key, value) ->
            Clog.v("Processing $relativeFile.swift")
            val structure = sourceKittenWrapper.proc("structure", "--file", file.absolutePath)
            val structureDoc = SourceKittenSubstructure.fromJson(jsonModule, structure)

            value.sourceFile = relativeFile
            structureDoc.sourceFile = relativeFile

            structureFile.writeText(structure)
            remapper.processSourceKittenModel(dir, key, value, structureDoc, jsonModule)
        }

        return null
    }
}
