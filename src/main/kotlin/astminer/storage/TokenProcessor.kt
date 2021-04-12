package astminer.storage

import astminer.cli.separateToken
import astminer.common.DEFAULT_TOKEN
import astminer.common.model.Node
import astminer.common.normalizeToken

/**
 * Each TokenProcessor processes a node's token and returns a new representation of it.
 * Before saving a token on the disk one usually processes the token with a TokenProcessor.
 */
enum class TokenProcessor {
    /**
     * Does not actually process the token, returns the original unchanged token.
     * Works like the identity function id: x --> x, hence the name.
     */
    LeaveOriginal {
        override fun processToken(node: Node): String = node.getToken()
    },

    /**
     * Splits the token into subtokens (words).
     * For example, "getFull_name" --> "get full name"
     */
    Split {
        override fun processToken(node: Node): String = separateToken(node.getToken())
    },

    /**
     * Processes the token according to the original code2vec implementation in order to match their behavior.
     */
    Normalize {
        override fun processToken(node: Node): String = normalizeToken(node.getToken(), DEFAULT_TOKEN)
    };

    abstract fun processToken(node: Node): String
}
