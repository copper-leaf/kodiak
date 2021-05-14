package com.copperleaf.kodiak.common.modules.impl.rootlocator

import com.copperleaf.kodiak.common.modules.ProjectRootLocator
import java.nio.file.Path

/**
 * Without a Git repository to signify the root of a project, we can use several other clues to determine the exact root
 * of our current project. Most projects have some standard files in the project root, things like READMEs, license
 * files, build scripts, or CI config files.
 */
class DefaultProjectRootLocator(
    val filesNormallyInProjectRoot: List<String> = defaultFilesNormallyInProjectRoot()
) : ProjectRootLocator {

    override fun getRootPath(input: Path): Path? {
        var currentDir: Path? = input
        while (currentDir != null) {
            if (currentDir.fileName.toString() == ".git") return currentDir
            currentDir = currentDir.parent
        }

        return null
    }

    companion object {
        fun defaultFilesNormallyInProjectRoot(): List<String> = listOf(
            "README.*",
            "package.json",
            "settings.gradle(.kts)?"
        )
    }
}
