package astminer.config

import astminer.problem.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class LabelExtractorConfig {
    abstract val labelExtractorImpl: LabelExtractor

    val granularity: Granularity
        get() = labelExtractorImpl.granularity

    abstract val serialName: String
}

/**
 * @see FileNameExtractor
 */
@Serializable
@SerialName("file name")
class FileNameExtractorConfig : LabelExtractorConfig() {
    @Transient
    override val labelExtractorImpl = FileNameExtractor
    @Transient
    override val serialName = "file name"
}

/**
 * @see FolderNameExtractor
 */
@Serializable
@SerialName("folder name")
class FolderNameExtractorConfig : LabelExtractorConfig() {
    @Transient
    override val labelExtractorImpl = FolderNameExtractor
    @Transient
    override val serialName = "folder name"
}

/**
 * @see FunctionNameLabelExtractor
 */
@Serializable
@SerialName("function name")
class FunctionNameExtractorConfig : LabelExtractorConfig() {
    @Transient
    override val labelExtractorImpl = FunctionNameLabelExtractor

    @Transient
    override val serialName = "function name"
}
