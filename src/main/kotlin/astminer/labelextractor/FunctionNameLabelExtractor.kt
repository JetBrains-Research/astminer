package astminer.labelextractor

import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionLabelExtractor
import astminer.common.model.LabeledResult
import astminer.common.model.Node

/**
 * Labels functions with their names.
 * Hides the name of the function in the subtree and also all in the recursive calls.
 */
object FunctionNameLabelExtractor : FunctionLabelExtractor {
    private const val HIDDEN_METHOD_NAME_TOKEN = "METHOD_NAME"

    override fun process(functionInfo: FunctionInfo<out Node>): LabeledResult<out Node>? {
        val normalizedName = functionInfo.nameNode?.token?.normalized ?: return null
        functionInfo.nameNode?.token?.technical = HIDDEN_METHOD_NAME_TOKEN
        return LabeledResult(functionInfo.root, normalizedName, functionInfo.filePath)
    }
}
