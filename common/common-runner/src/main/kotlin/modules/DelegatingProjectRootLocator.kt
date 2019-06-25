package com.copperleaf.kodiak.common.modules

import com.copperleaf.kodiak.common.firstOrNullBy
import java.nio.file.Path

/**
 * Delegates to a list of other `ProjectRootLocator`s, returning the first one which has a valid result.
 */
internal class DelegatingProjectRootLocator(
    private val locators: List<ProjectRootLocator>
) : ProjectRootLocator {

    override fun getRootPath(input: Path) : Path? {
        return locators.firstOrNullBy { it.getRootPath(input) }
    }

}