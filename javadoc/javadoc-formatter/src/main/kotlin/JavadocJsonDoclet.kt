package com.copperleaf.kodiak.java

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.common.connectAllToParents
import com.copperleaf.kodiak.java.formatter.toClassDoc
import com.copperleaf.kodiak.java.formatter.toPackageDoc
import com.sun.javadoc.ClassDoc
import com.sun.javadoc.LanguageVersion
import com.sun.javadoc.PackageDoc
import com.sun.javadoc.RootDoc
import com.sun.tools.doclets.standard.Standard
import java.io.File

class JavadocJsonDoclet {
    companion object {

        /**
         * NOTE: Without this method present and returning LanguageVersion.JAVA_1_5,
         * Javadoc will not process generics because it assumes LanguageVersion.JAVA_1_1
         *
         * @return language version (hard coded to LanguageVersion.JAVA_1_5)
         */
        @JvmStatic
        fun languageVersion(): LanguageVersion {
            return LanguageVersion.JAVA_1_5
        }

        @JvmStatic
        fun optionLength(optionFlag: String): Int {
            if (optionFlag == "-d") {
                return 2
            }

            return Standard.optionLength(optionFlag)
        }


// Entry points, main routines
//----------------------------------------------------------------------------------------------------------------------

        @JvmStatic
        fun start(rootDoc: RootDoc): Boolean {
            var destinationDir = rootDoc.options().find { it.firstOrNull() == "-d" }!![1]
            if (destinationDir.endsWith("/")) {
                destinationDir = destinationDir.dropLast(1)
            } else if (destinationDir.endsWith("\\")) {
                destinationDir = destinationDir.dropLast(1)
            }

            val classes = mutableSetOf<ClassDoc>()
            val packages = mutableSetOf<PackageDoc>()

            for (classDoc in rootDoc.classes()) {
                classes.add(classDoc)
                packages.add(classDoc.containingPackage())
            }

            classes.forEach { classdoc ->
                Clog.i("Loading classdoc [${classdoc.name()}]")
                val file = File("$destinationDir/Class/${classdoc.qualifiedTypeName().replace(".", "/")}.json")
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                try {
                    file.writeText(classdoc.toClassDoc(true).toJson())
                } catch (e: Exception) {
                    Clog.e("Failed to create json for classdoc [${classdoc.name()}]: ${e.message}")
                }
            }

            connectAllToParents(
                // create initial packages, use common functionality to connect parent-child structures
                packages.mapNotNull { packagedoc ->
                    Clog.i("Loading packagedoc [${packagedoc.name()}]")
                    try {
                        packagedoc.toPackageDoc(true)
                    } catch (e: Exception) {
                        Clog.e("Failed to create json for packagedoc [${packagedoc.name()}]: ${e.message}")
                        null
                    }
                },
                {
                    it.item.copy(
                        parent = it.parentId ?: "",
                        subpackages = it.children.map { (it.item.node as PackageDoc).toPackageDoc(false) }
                    )
                },
                { it.id }
            ).forEach {
                // write package files to disk
                val file = File("$destinationDir/Package/${it.id.replace(".", "/")}/index.json")
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.writeText(it.toJson())
            }

            return true
        }
    }
}
