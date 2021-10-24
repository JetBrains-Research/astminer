package astminer.common.model

class Token(
    val original: String?,
    val range: TokenRange?,
    private val normalization: Normalization = Code2VecNormalization
) {
    val final: String
        get() = technical ?: normalized

    var technical: String? = null

    val normalized = normalization.normalizeToken(original)

    override fun toString(): String = final
}

typealias Line = Int
typealias Column = Int

data class TokenRange(val start: Pair<Line, Column>, val end: Pair<Line, Column>)
