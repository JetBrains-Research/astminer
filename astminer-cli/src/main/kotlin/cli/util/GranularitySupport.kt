package cli.util

import cli.FileParser
import cli.GranularityParser
import cli.MethodParser

/**
 * @param granularity class that implements granularity parsing
 * @param level level of granularity
 */
data class SupportedGranularityLevel(val granularity: GranularityParser, val level: String)

fun getGranularityParser(granularityLevel: String, splitTokens: Boolean, hideMethodName: Boolean): GranularityParser {
    when (granularityLevel) {
        "file" -> return FileParser(splitTokens)
        "method" -> return MethodParser(splitTokens, hideMethodName)
    }
    throw UnsupportedOperationException("Unsupported granularity level $granularityLevel")
}

data class GranularityParseResult<T>(val filePath: String, val root: T?, val label: String = "")
