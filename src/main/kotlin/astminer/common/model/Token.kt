package astminer.common.model

import astminer.common.TokenNormalization

class Token(val original: String?) {
    val final: String
        get() = technical ?: normalized

    var technical: String? = null

    val normalized = TokenNormalization.normalizeToken(original)

    override fun toString(): String = final
}
