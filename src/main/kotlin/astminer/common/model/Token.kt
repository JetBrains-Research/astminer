package astminer.common.model

import astminer.common.*

/**
 * Class to wrap logic with token processing.
 * It is responsible for token normalization or replacing it with technical information.
 * Use `token.original` to access the original token.
 */
class Token(val original: String?) {
    /**
     * Technical token is used to shadow the original token with mining pipeline specific value.
     * For example, for the method name prediction problem
     * we want to set technical `<METHOD_NAME>` token to hide real method name.
     */
    var technical: String? = null

    /**
     * Original token with normalization applied
     * @see normalizeToken
     */
    val normalized = run {
        if (original == null) return@run EMPTY_TOKEN
        val subTokens = splitToSubtokens(original)
        if (subTokens.isEmpty()) EMPTY_TOKEN else subTokens.joinToString(TOKEN_DELIMITER)
    }

    /**
     * Access to the final representation of the token after normalization and other preprocessing.
     * It returns technical assign token if it exists or normalized token otherwise.
     * @see technical
     * @see normalized
     */
    fun final() = technical ?: normalized

    override fun toString(): String = final()
}
