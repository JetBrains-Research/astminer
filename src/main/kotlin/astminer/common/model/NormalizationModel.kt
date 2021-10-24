package astminer.common.model

interface Normalization {
    fun normalizeToken(token: String?): String
}

object Code2VecNormalization: Normalization {
    const val EMPTY_TOKEN = "EMPTY"
    const val TOKEN_DELIMITER = "|"

    override fun normalizeToken(token: String?): String {
        if (token == null) return EMPTY_TOKEN
        val subTokens = splitToSubtokens(token)
        return if (subTokens.isEmpty()) EMPTY_TOKEN else subTokens.joinToString(TOKEN_DELIMITER)
    }

    /**
     * The function was adopted from the original code2vec implementation in order to match their behavior:
     * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
     */
    fun splitToSubtokens(token: String) = token
        .trim()
        .split(splitRegex)
        .map { s -> normalizeSubToken(s, "") }
        .filter { it.isNotEmpty() }
        .toList()

    private val splitRegex = "(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+".toRegex()


    /**
     * The function was adopted from the original code2vec implementation in order to match their behavior:
     * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
     */
    fun normalizeSubToken(token: String, defaultToken: String): String {
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

    private val newLineReg = "\\\\n".toRegex()
    private val whitespaceReg = "//s+".toRegex()
    private val quotesApostrophesCommasReg = "[\"',]".toRegex()
    private val unicodeWeirdCharReg = "\\P{Print}".toRegex()
    private val notALetterReg = "[^A-Za-z]".toRegex()
}