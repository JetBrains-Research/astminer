package astminer.parse.javaparser

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter
import mu.KotlinLogging

class JavaparserMethodSplitter : TreeFunctionSplitter<JavaParserNode> {
    private val methodDeclarationType = "Mth"
    private val logger = KotlinLogging.logger("Javaparser - Function splitting")

    override fun splitIntoFunctions(root: JavaParserNode, filePath: String): Collection<FunctionInfo<JavaParserNode>> {
        val methods = mutableListOf<FunctionInfo<JavaParserNode>>()
        for (methodRoot in root.preOrder().filter { it.typeLabel == methodDeclarationType }) {
            try {
                methods.add(JavaparserFunctionInfo(methodRoot, filePath))
            } catch (e: IllegalStateException) {
                logger.warn("Couldn't collect information about the function: ${e.message} in file $filePath")
            }
        }
        return methods
    }
}
