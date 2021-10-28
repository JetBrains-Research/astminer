package astminer.common.model

import astminer.common.Code2VecNormalization

class Token(
    val original: String?,
    private val normalization: Normalization = Code2VecNormalization
) {
    val final: String
        get() = technical ?: normalized

    var technical: String? = null

    val normalized = normalization.normalizeToken(original)

    override fun toString(): String = final
}
