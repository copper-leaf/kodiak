package com.copperleaf.kodiak.common.modules

import java.nio.file.Path

/**
 * Implementations of this interface will take a given Path and return another path that is the "root" of the project
 * which contains the original path. The Path returned should be either the input Path, or a parent directory of the
 * input Path, but not a child.
 */
interface ProjectRootLocator {

    /**
     * Return the project root given an arbitrary path expected to be in that root. Returns null if the locator is
     * unable to determine the project root given the input path.
     */
    fun getRootPath(input: Path): Path?

    companion object {
        fun from(vararg locators: ProjectRootLocator): ProjectRootLocator {
            return DelegatingProjectRootLocator(locators.toList())
        }
    }
}
