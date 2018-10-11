package com.copperleaf.javadoc.json

import com.copperleaf.javadoc.json.formatter.toClassDoc
import com.copperleaf.javadoc.json.formatter.toPackageDoc
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
            if(optionFlag == "-d") {
                return 2
            }

            return Standard.optionLength(optionFlag)
        }


// Entry points, main routines
//----------------------------------------------------------------------------------------------------------------------

        @JvmStatic
        fun start(rootDoc: RootDoc): Boolean {
            var destinationDir = rootDoc.options().find { it.firstOrNull() == "-d" }!![1]
            if(destinationDir.endsWith("/")) {
                destinationDir = destinationDir.dropLast(1)
            }
            else if(destinationDir.endsWith("\\")) {
                destinationDir = destinationDir.dropLast(1)
            }

            val classes = HashSet<ClassDoc>()
            val packages = HashSet<PackageDoc>()

            for (classDoc in rootDoc.classes()) {
                classes.add(classDoc)
                packages.add(classDoc.containingPackage())
            }

            for(classdoc in classes) {
                val file = File("$destinationDir/${classdoc.qualifiedTypeName().replace(".", "/")}.json")
                if(!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.writeText(classdoc.toClassDoc().toJson())
            }

            for(packagedoc in packages) {
                val file = File("$destinationDir/${packagedoc.name().replace(".", "/")}/index.json")
                if(!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.writeText(packagedoc.toPackageDoc().toJson())
            }

            return true
        }
    }
}