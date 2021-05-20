package astminer.config

import astminer.filters.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A config for filter that tests files (ParseResult)
 */
@Serializable
sealed class FileFilterConfig {
    abstract val filter: FileFilter
}

/**
 * @see TreeSizeFilter
 */
@Serializable
@SerialName("max tree size")
data class FileTreeSizeFilterConfig(val maxTreeSize: Int) : FileFilterConfig() {
    @Transient
    override val filter = TreeSizeFilter(maxTreeSize)
}

/**
 * A config for filter that tests functions (FunctionInfo)
 */
@Serializable
sealed class FunctionFilterConfig {
    abstract val filter: FunctionFilter
}

/**
 * @see TreeSizeFilter
 */
@Serializable
@SerialName("max tree size")
data class FunctionTreeSizeFilterConfig(val maxTreeSize: Int) : FunctionFilterConfig() {
    @Transient
    override val filter = TreeSizeFilter(maxTreeSize)
}

/**
 * @see ModifierFilter
 */
@Serializable
@SerialName("exclude functions with modifiers")
data class ModifierFilterConfig(val modifiers: List<String>) : FunctionFilterConfig() {
    @Transient
    override val filter = ModifierFilter(modifiers)
}

/**
 * @see AnnotationFilter
 */
@Serializable
@SerialName("exclude functions with annotations")
data class AnnotationFilterConfig(val annotations: List<String>) : FunctionFilterConfig() {
    @Transient
    override val filter = AnnotationFilter(annotations)
}

/**
 * @see ConstructorFilter
 */
@Serializable
@SerialName("exclude constructors")
class ConstructorFilterConfig : FunctionFilterConfig() {
    @Transient
    override val filter = ConstructorFilter
}

/**
 * @see FunctionNameWordsNumberFilter
 */
@Serializable
@SerialName("by function name length")
data class FunctionNameWordsNumberFilterConfig(val maxWordsNumber: Int) : FunctionFilterConfig() {
    @Transient
    override val filter = FunctionNameWordsNumberFilter(maxWordsNumber)
}

/**
 * @see FunctionAnyNodeWordsNumberFilter
 */
@Serializable
@SerialName("by length of any token")
data class FunctionAnyNodeWordsNumberFilterConfig(val maxWordsNumber: Int) : FunctionFilterConfig() {
    @Transient
    override val filter = FunctionAnyNodeWordsNumberFilter(maxWordsNumber)
}
