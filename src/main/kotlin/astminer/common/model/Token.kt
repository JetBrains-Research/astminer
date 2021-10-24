package astminer.common.model

data class Token(
    val original: String?,
    val range: TokenRange?,
    val normalization: Normalization = Code2VecNormalization
) {
    init {
        if (original == null) require(range == null) { "Token range without token was provided" }
    }

    val final: String
        get() = technical ?: normalized

    var technical: String? = null

    val normalized = Code2VecNormalization.normalizeToken(original)

    override fun toString(): String = final
}

typealias Line = Int
typealias Column = Int

data class TokenRange(val start: Pair<Line, Column>, val end: Pair<Line, Column>) {
    init {
        require(start.first >= end.first) { "Wrong line format" }
        require(start.second >= end.second) { "Wrong column format" }
    }
}

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