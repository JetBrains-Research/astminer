package astminer.storage

import astminer.common.DEFAULT_TOKEN
import astminer.common.getTechnicalToken
import astminer.common.model.Node
import astminer.common.normalizeToken
import astminer.common.splitToSubtokens

/**
 * Each TokenProcessor processes a node's token and returns a new representation of it. *It respects technical tokens*.
 * Before saving a token on the disk one usually processes the token with a TokenProcessor.
 */
enum class TokenProcessor {
    /**
     * Splits the token into subtokens (words).
     * For example, "getFull_name" --> "get|full|name"
     */
    Split {
        private fun separateToken(token: String): String {
            return splitToSubtokens(token).joinToString("|")
        }

        override fun processToken(node: Node): String = separateToken(node.token)
    },

    /**
     * Processes the token according to the original code2vec implementation in order to match their behavior.
     */
    Normalize {
        override fun processToken(node: Node): String = normalizeToken(node.token, DEFAULT_TOKEN)
    };

    protected abstract fun processToken(node: Node): String

    /**
     * Returns technical token, if technical token is set. Returns processed original token otherwise.
     */
    fun getPresentableToken(node: Node) = node.getTechnicalToken() ?: processToken(node)
}
