package astminer.common

const val EMPTY_TOKEN = "<E>"
const val TOKEN_DELIMITER = "|"
const val EMPTY_STRING = ""

/**
 * Splits token into subtokens by commonly used practice, i.e. `camelCase` or `snake_case`.
 * Returns a list of not empty, normalized subtokens.
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 * @see normalizeToken
 */
fun splitToSubtokens(token: String) = token
    .trim()
    .split(splitRegex)
    .map { s -> normalizeToken(s, EMPTY_STRING) }
    .filter { it.isNotEmpty() }
    .toList()

private val splitRegex = "(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+".toRegex()

/**
 * Normalize token by conversion to lower case, removing the new line,
 * whitespace, quotes, and other weird Unicode characters.
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 * @param token Token to normalize
 * @param defaultToken If the token is empty after the normalization process, it will be replaced with the default token
 */
fun normalizeToken(token: String, defaultToken: String): String {
    val cleanToken = token.lowercase()
        .replace(newLineReg, EMPTY_STRING) // escaped new line
        .replace(whitespaceReg, EMPTY_STRING) // whitespaces
        .replace(quotesApostrophesCommasReg, EMPTY_STRING) // quotes, apostrophes, commas
        .replace(unicodeWeirdCharReg, EMPTY_STRING) // unicode weird characters

    val stripped = cleanToken.replace(notALetterReg, EMPTY_STRING)

    return stripped.ifEmpty {
        val carefulStripped = cleanToken.replace(" ", "_")
        carefulStripped.ifEmpty {
            defaultToken
        }
    }
}

private val newLineReg = "\\\\n".toRegex()
private val whitespaceReg = "//s+".toRegex()
private val quotesApostrophesCommasReg = "[\"',]".toRegex()
private val unicodeWeirdCharReg = "\\P{Print}".toRegex()
private val notALetterReg = "[^A-Za-z]".toRegex()
