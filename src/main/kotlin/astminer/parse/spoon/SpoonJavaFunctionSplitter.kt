package astminer.parse.spoon

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter
import mu.KotlinLogging

class SpoonJavaFunctionSplitter : TreeFunctionSplitter<SpoonNode> {
    private val methodDeclaration = "Method"
    private val logger = KotlinLogging.logger("Spoon function splitting")

    override fun splitIntoFunctions(root: SpoonNode, filePath: String): Collection<FunctionInfo<SpoonNode>> {
        return root.preOrder().filter { it.typeLabel == methodDeclaration }
            .mapNotNull {
                try {
                    SpoonJavaFunctionInfo(it, filePath)
                } catch (e: IllegalStateException) {
                    logger.warn { "Couldn't parse function in file $filePath. Error occured: ${e.message} " }
                    null
                }
            }
    }
}
