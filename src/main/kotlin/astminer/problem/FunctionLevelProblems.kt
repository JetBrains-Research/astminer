package astminer.problem

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.setTechnicalToken

interface FunctionLevelProblem {
    fun process(functionInfo: FunctionInfo<Node>): LabeledResult<Node>?
}

object MethodNameExtractor : FunctionLevelProblem {
    override fun process(functionInfo: FunctionInfo<Node>): LabeledResult<Node>? {
        val name = functionInfo.name ?: return null
        functionInfo.root.preOrder().forEach { node ->
            if (node.getToken() == name) {
                node.setTechnicalToken("SELF")
            }
        }
        functionInfo.nameNode?.setTechnicalToken("METHOD_NAME")
        return LabeledResult(functionInfo.root, name, functionInfo.filePath)
    }
}