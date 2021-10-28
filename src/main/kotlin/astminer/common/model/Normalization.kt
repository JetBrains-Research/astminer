package astminer.common.model

interface Normalization {
    fun normalizeToken(token: String?): String
}
