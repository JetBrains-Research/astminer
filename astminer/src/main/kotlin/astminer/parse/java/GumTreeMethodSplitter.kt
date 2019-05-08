package astminer.parse.java

import com.github.gumtreediff.tree.ITree
import com.github.gumtreediff.tree.TreeContext
import astminer.common.TreeSplitter
import astminer.common.preOrder


data class MethodInfo(val enclosingClassName: String, val methodName: String, val parameterTypes: List<String>)

const val METHOD_INFO_KEY = "method_info"

fun GumTreeJavaNode.setMethodInfo(methodInfo: MethodInfo) {
    this.setMetadata(METHOD_INFO_KEY, methodInfo)
}

fun GumTreeJavaNode.getMethodInfo(): MethodInfo? {
    return this.getMetadata(METHOD_INFO_KEY) as? MethodInfo
}


class GumTreeMethodSplitter : TreeSplitter<GumTreeJavaNode> {

    companion object {
        private object TypeLabels {
            const val methodDeclaration = "MethodDeclaration"
            const val simpleName = "SimpleName"
            const val typeDeclaration = "TypeDeclaration"
            const val singleVariableDeclaration = "SingleVariableDeclaration"
        }
    }

    private fun getMethodNodes(treeContext: TreeContext): List<ITree> {
        return treeContext.root.descendants
                .filter { treeContext.getTypeLabel(it.type) == TypeLabels.methodDeclaration }
    }

    private fun getMethodName(methodNode: ITree, context: TreeContext): String {
        val nameNode = methodNode.children.firstOrNull { context.getTypeLabel(it.type) == TypeLabels.simpleName }
        return nameNode?.label ?: ""
    }

    private fun getEnclosingClassName(methodNode: ITree, context: TreeContext): String {
        val classDeclarationNode = methodNode.parents.firstOrNull {
            context.getTypeLabel(it.type) == TypeLabels.typeDeclaration
        } ?: return ""
        val nameNode = classDeclarationNode.children.firstOrNull {
            context.getTypeLabel(it.type) == TypeLabels.simpleName
        }
        return nameNode?.label ?: ""
    }

    private fun getParameterTypes(methodNode: ITree, context: TreeContext): List<String> {
        val result: MutableList<String> = ArrayList()
        methodNode.children
                .filter { context.getTypeLabel(it.type) == TypeLabels.singleVariableDeclaration }
                .forEach { node ->
                    node.children.firstOrNull { c ->
                        context.getTypeLabel(c.type).endsWith("Type")
                    }?.let { typeNode ->
                        result.add(typeNode.label)
                    }
                }
        return result
    }

    private fun getMethodInfo(methodNode: ITree, context: TreeContext): MethodInfo {
        return MethodInfo(getEnclosingClassName(methodNode, context),
                getMethodName(methodNode, context),
                getParameterTypes(methodNode, context))
    }

    override fun split(root: GumTreeJavaNode): Collection<GumTreeJavaNode> {
        val rawMethodNodes = getMethodNodes(root.context).toSet()

        val splitNodes: MutableList<GumTreeJavaNode> = ArrayList()

        root.preOrder().forEach {
            val gtNode = (it as GumTreeJavaNode).wrappedNode
            if (gtNode in rawMethodNodes) {
                val methodInfo = getMethodInfo(gtNode, root.context)
                it.setMethodInfo(methodInfo)
                splitNodes.add(it)
            }
        }

        return splitNodes
    }

}