package astminer.pipeline.branch

import astminer.problem.Granularity

/**
 * This exception is thrown when problem granularity is implemented incorrectly or the problem is not specified
 * inside the correct pipeline branch.
 */
class ProblemDefinitionException(granularity: Granularity, problemName: String) :
    IllegalStateException("Problem `$problemName` has invalid granularity $granularity")

/**
 * This exception is thrown when the given filter is not implemented for the given granularity
 */
class IllegalFilterException(granularity: Granularity, filterName: String):
        IllegalStateException("Unknown filter `$filterName` for granularity $granularity")