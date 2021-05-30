package astminer.pipeline.branch

import astminer.problem.Granularity

/**
 * This exception is thrown when label extractor's granularity is implemented incorrectly.
 */
class ProblemDefinitionException(granularity: Granularity, problemName: String) :
    IllegalStateException("Problem `$problemName` has invalid granularity $granularity")

/**
 * This exception is thrown when the given filter is not implemented for the given granularity
 */
class IllegalFilterException(granularity: Granularity, filterName: String):
        IllegalStateException("Unknown filter `$filterName` for granularity $granularity")