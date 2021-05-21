package astminer.common

import astminer.common.model.Node
import astminer.storage.TokenProcessor
import java.util.ArrayList


const val DEFAULT_TOKEN = "EMPTY_TOKEN"
const val TECHNICAL_TOKEN_KEY = "technical_token"

/**
 * Sets a node's technical token.
 * Technical tokens do not have to represent original tokens.
 * @see TokenProcessor and how it treats technical tokens
 */
fun Node.setTechnicalToken(token: String) {
    metadata[TECHNICAL_TOKEN_KEY] = token
}

/**
 * Get a node's technical token.
 * @see setTechnicalToken for more
 */
fun Node.getTechnicalToken(): String? = metadata[TECHNICAL_TOKEN_KEY]?.toString()

/**
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 */
fun normalizeToken(token: String, defaultToken: String): String {
    val cleanToken = token.toLowerCase()
            .replace("\\\\n".toRegex(), "") // escaped new line
            .replace("//s+".toRegex(), "") // whitespaces
            .replace("[\"',]".toRegex(), "") // quotes, apostrophies, commas
            .replace("\\P{Print}".toRegex(), "") // unicode weird characters

    val stripped = cleanToken.replace("[^A-Za-z]".toRegex(), "")

    return if (stripped.isEmpty()) {
        val carefulStripped = cleanToken.replace(" ", "_")
        if (carefulStripped.isEmpty()) {
            defaultToken
        } else {
            carefulStripped
        }
    } else {
        stripped
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
