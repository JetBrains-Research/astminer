package astminer.config

import astminer.filters.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class FileFilterConfig {
    abstract val filter: FileFilter
}

@Serializable
data class FileTreeSizeFilterConfig(val maxTreeSize: Int) : FileFilterConfig() {
    @Transient
    override val filter = FileTreeSizeFilter(maxTreeSize)
}

@Serializable
sealed class FunctionFilterConfig {
    abstract val filter: FunctionFilter
}

@Serializable
data class ModifierFilterConfig(val excludeModifiers: List<String>) : FunctionFilterConfig() {
    @Transient
    override val filter = ModifierFilter(excludeModifiers)
}

@Serializable
data class AnnotationFilterConfig(val excludeAnnotations: List<String>) : FunctionFilterConfig() {
    @Transient
    override val filter = AnnotationFilter(excludeAnnotations)
}

@Serializable
object ConstructorFilterConfig : FunctionFilterConfig() {
    @Transient
    override val filter = ConstructorFilter
}

@Serializable
data class FunctionNameWordsNumberFilterConfig(val maxWordsNumber: Int) : FunctionFilterConfig() {
    @Transient
    override val filter = FunctionNameWordsNumberFilter(maxWordsNumber)
}

@Serializable
data class FunctionAnyNodeWordsNumberFilterConfig(val maxWordsNumber: Int) : FunctionFilterConfig() {
    @Transient
    override val filter = FunctionAnyNodeWordsNumberFilter(maxWordsNumber)
}
