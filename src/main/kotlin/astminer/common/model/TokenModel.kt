package astminer.common.model

data class Token(
    val original: String?,
    val range: TokenRange?,
    val normalization: Normalization = Code2VecNormalization
) {
    init {
        if (original == null) require(range == null) { "Token range without token was provided" }
    }

    val final: String
        get() = technical ?: normalized

    var technical: String? = null

    val normalized = normalization.normalizeToken(original)

    override fun toString(): String = final
}

typealias Line = Int
typealias Column = Int

data class TokenRange(val start: Pair<Line, Column>, val end: Pair<Line, Column>) {
    init {
        require(start.first >= end.first) { "Wrong line format" }
        require(start.second >= end.second) { "Wrong column format" }
    }
}
