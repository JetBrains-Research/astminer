package astminer.problem

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.setTechnicalToken

interface FunctionLevelProblem {
    fun process(functionInfo: FunctionInfo<out Node>): LabeledResult<out Node>?
}

/**
 * Labels functions with their names.
 * Hides the name of the function in the subtree and also all in the recursive calls.
 */
object FunctionNameProblem : FunctionLevelProblem {
    const val TECHNICAL_METHOD_NAME = "METHOD_NAME"
    const val TECHNICAL_RECURSIVE_CALL = "SELF"

    override fun process(functionInfo: FunctionInfo<out Node>): LabeledResult<out Node>? {
        val name = functionInfo.name ?: return null
        functionInfo.root.preOrder().forEach { node ->
            if (node.getToken() == name) {
                node.setTechnicalToken(TECHNICAL_RECURSIVE_CALL)
            }
        }
        functionInfo.nameNode?.setTechnicalToken(TECHNICAL_METHOD_NAME)
        return LabeledResult(functionInfo.root, name, functionInfo.filePath)
    }
}
