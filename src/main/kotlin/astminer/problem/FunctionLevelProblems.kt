package astminer.problem

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.setTechnicalToken

interface FunctionLevelProblem : Problem<FunctionInfo<out Node>> {
    override fun process(entity: FunctionInfo<out Node>): LabeledResult<out Node>?
}

object MethodNameExtractor : FunctionLevelProblem {
    override fun process(entity: FunctionInfo<out Node>): LabeledResult<out Node>? {
        val name = entity.name ?: return null
        entity.root.preOrder().forEach { node ->
            if (node.getToken() == name) {
                node.setTechnicalToken("SELF")
            }
        }
        entity.nameNode?.setTechnicalToken("METHOD_NAME")
        return LabeledResult(entity.root, name, entity.filePath)
    }
}
