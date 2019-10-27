package com.copperleaf.kodiak.swift

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.JsonableDocElement
import com.copperleaf.kodiak.common.SpecializedDocElement
import com.copperleaf.kodiak.swift.formatter.isSuppressed
import com.copperleaf.kodiak.swift.formatter.toSourceFile
import com.copperleaf.kodiak.swift.internal.models.SourceKittenFile
import com.copperleaf.kodiak.swift.internal.models.SourceKittenSubstructure
import java.io.File
import java.nio.file.Path

class SwiftdocRemapper(
    private val mainArgs: MainArgs
) {

    fun processSourceKittenModel(
        dir: Path,
        sourceFileName: String,
        model: SourceKittenFile,
        structure: SourceKittenSubstructure
    ) : Map<Class<DocElement>, List<DocElement>> {
        // create child pages for the elements contained within this source file
        val sourceFileStructures = model.substructures.map {
            val matchingStructure = it.findMatch(structure)
            if(matchingStructure != null && !it.isSuppressed()) {
                processSourceKittenModel(dir, sourceFileName, it to matchingStructure)
            }
            else null
        }
        .filterNotNull()
        .groupBy { it.javaClass }

        // create the sourceFile doc and write it to disk
        val relativeFilePath = File(sourceFileName)
            .relativeTo(dir.toFile())
            .path
            .removeSuffix(".swift")
            .replace(".", "/")

        val file = newFile("${mainArgs.output}/SourceFile/$relativeFilePath/index.json")

        model.sourceFile = "$relativeFilePath.swift"
        file.writeText(model.toSourceFile(sourceFileStructures).toJson())

        return sourceFileStructures
    }

    private fun processSourceKittenModel(
        dir: Path,
        sourceFileName: String,
        model: Pair<SourceKittenSubstructure, SourceKittenSubstructure>
    ): DocElement? {
        val relativeFilePath = File(sourceFileName)
            .relativeTo(dir.toFile())
            .path
            .removeSuffix(".swift")
            .replace(".", "/")

        val docModel = model.first.format(model.second)

        if(docModel != null && docModel is JsonableDocElement) {
            val baseDir = if(docModel is SpecializedDocElement) "${docModel.kind}/${docModel.subKind}" else docModel.kind

            val file = newFile("${mainArgs.output}/$baseDir/$relativeFilePath/${model.first.name}.json")
            file.writeText(docModel.toJson())
        }
        else {
            if(docModel?.kind !in listOf("Field", "Method", "Typealias", "Extension")) {
                Clog.v("Unknown model type: ${docModel?.name} (${docModel?.kind})")
            }
        }

        return docModel
    }
}
