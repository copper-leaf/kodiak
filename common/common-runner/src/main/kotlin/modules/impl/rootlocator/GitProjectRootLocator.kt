package com.copperleaf.kodiak.common.modules.impl.rootlocator

import com.copperleaf.kodiak.common.modules.ProjectRootLocator
import java.nio.file.Path

/**
 * Looks for a `.git` directory to signify the project root.
 */
class GitProjectRootLocator : ProjectRootLocator {

    override fun getRootPath(input: Path) : Path? {
        var currentDir: Path? = input
        while(currentDir != null) {
            if(currentDir.fileName.toString() == ".git") return currentDir
            currentDir = currentDir.parent
        }

        return null
    }

}