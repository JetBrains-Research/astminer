package astminer.parse

class ParsingException(parserType: String, language: String, exc: Exception? = null) :
    IllegalStateException("Parser $parserType had problems parsing $language: ${exc?.message ?: "Unknown error."}")
