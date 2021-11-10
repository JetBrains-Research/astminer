package astminer.common.model

import astminer.common.normalizeToken

class Token(val original: String?) {
    /** Final token after all normalizations and shadowing
     * @see technical
     * @see normalized **/
    val final: String
        get() = technical ?: normalized

    /** Token that shadows any original or normalized token
     * and have the most priority in calculating final token
     * that will be saved. It can be useful when it's necessary to hide something
     * (for example method name in method name prediction problem) **/
    var technical: String? = null

    /** Original token after string normalization
     * @see normalizeToken **/
    val normalized = normalizeToken(original)

    override fun toString(): String = final
}
