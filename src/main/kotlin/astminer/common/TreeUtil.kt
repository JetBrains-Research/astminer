package astminer.common

const val EMPTY_TOKEN = "EMPTY"

/**
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 */

val newLineReg = "\\\\n".toRegex()
val whitespaceReg = "//s+".toRegex()
val quotesApostrophesCommasReg = "[\"',]".toRegex()
val unicodeWeirdCharReg = "\\P{Print}".toRegex()
val notALetterReg = "[^A-Za-z]".toRegex()

fun normalizeToken(token: String, defaultToken: String): String {
    val cleanToken = token.lowercase()
        .replace(newLineReg, "") // escaped new line
        .replace(whitespaceReg, "") // whitespaces
        .replace(quotesApostrophesCommasReg, "") // quotes, apostrophes, commas
        .replace(unicodeWeirdCharReg, "") // unicode weird characters

    val stripped = cleanToken.replace(notALetterReg, "")

    return stripped.ifEmpty {
        val carefulStripped = cleanToken.replace(" ", "_")
        carefulStripped.ifEmpty {
            defaultToken
        }
    }
}

/**
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 */

val splitRegex = "(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+".toRegex()

fun splitToSubtokens(token: String) = token
    .trim()
    .split(splitRegex)
    .map { s -> normalizeToken(s, "") }
    .filter { it.isNotEmpty() }
    .toList()
