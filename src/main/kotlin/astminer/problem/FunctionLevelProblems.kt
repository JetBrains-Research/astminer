package astminer.problem

import astminer.common.model.FunctionInfo
import astminer.common.model.Node

interface FunctionLevelProblem {
    fun process(functionInfo: FunctionInfo<out Node>): LabeledResult<out Node>?
}

/**
 * Labels functions with their names.
 * Hides the name of the function in the subtree and also all in the recursive calls.
 */
object FunctionNameProblem : FunctionLevelProblem {
    const val HIDDEN_METHOD_NAME_TOKEN = "METHOD_NAME"
    const val RECURSIVE_CALL_TOKEN = "SELF"

    override fun process(functionInfo: FunctionInfo<out Node>): LabeledResult<out Node>? {
        val normalizedName = functionInfo.nameNode?.normalizedToken ?: return null
        functionInfo.root.preOrder().forEach { node ->
            if (node.originalToken == functionInfo.name) {
                node.technicalToken = RECURSIVE_CALL_TOKEN
            }
        }
        functionInfo.nameNode?.technicalToken = HIDDEN_METHOD_NAME_TOKEN
        return LabeledResult(functionInfo.root, normalizedName, functionInfo.filePath)
    }
}
