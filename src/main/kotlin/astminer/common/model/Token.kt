package astminer.common.model

import astminer.common.normalizeToken

class Token(val original: String?) {
    val final: String
        get() = technical ?: normalized

    var technical: String? = null

    val normalized = normalizeToken(original)

    override fun toString(): String = final
}
