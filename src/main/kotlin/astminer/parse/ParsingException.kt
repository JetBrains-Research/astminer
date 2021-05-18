package astminer.parse

class ParsingException(parserType: String, language: String, message: String? = null) :
    IllegalStateException("Parser $parserType had problems parsing $language: ${message ?: "Unknown error."}")
