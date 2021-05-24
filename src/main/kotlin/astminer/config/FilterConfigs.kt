package astminer.config

import astminer.filters.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base class for all filter configs. See below
 */
@Serializable
sealed class FilterConfig {
    abstract val serialName: String
}

/**
 * @see TreeSizeFilter
 */
@Serializable
@SerialName("by tree size")
data class TreeSizeFilterConfig(val maxTreeSize: Int) : FilterConfig() {
    override val serialName = "by tree size"
}

/**
 * @see ModifierFilter
 */
@Serializable
@SerialName("by modifiers")
data class ModifierFilterConfig(val modifiers: List<String>) : FilterConfig() {
    override val serialName = "by modifiers"
}

/**
 * @see AnnotationFilter
 */
@Serializable
@SerialName("by annotations")
data class AnnotationFilterConfig(val annotations: List<String>) : FilterConfig() {
    override val serialName = "by annotations"
}

/**
 * @see ConstructorFilter
 */
@Serializable
@SerialName("no constructors")
object ConstructorFilterConfig : FilterConfig() {
    override val serialName = "no constructors"
}

/**
 * @see FunctionNameWordsNumberFilter
 */
@Serializable
@SerialName("by function name length")
data class FunctionNameWordsNumberFilterConfig(val maxWordsNumber: Int) : FilterConfig() {
    override val serialName = "by function name length"
}

/**
 * @see WordsNumberFilter
 */
@Serializable
@SerialName("by words number")
data class WordsNumberFilterConfig(val maxTokenWordsNumber: Int) : FilterConfig() {
    override val serialName = "by words number"
}
