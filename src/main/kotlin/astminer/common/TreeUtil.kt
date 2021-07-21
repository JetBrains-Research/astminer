package astminer.common

const val DEFAULT_TOKEN = "EMPTY"

/**
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 */
fun normalizeToken(token: String, defaultToken: String): String {
    val cleanToken = token.lowercase()
            .replace("\\\\n".toRegex(), "") // escaped new line
            .replace("//s+".toRegex(), "") // whitespaces
            .replace("[\"',]".toRegex(), "") // quotes, apostrophes, commas
            .replace("\\P{Print}".toRegex(), "") // unicode weird characters

    val stripped = cleanToken.replace("[^A-Za-z]".toRegex(), "")

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
fun splitToSubtokens(token: String) = token
        .trim()
        .split("(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+".toRegex())
        .map { s -> normalizeToken(s, "") }
        .filter { it.isNotEmpty() }
        .toList()
