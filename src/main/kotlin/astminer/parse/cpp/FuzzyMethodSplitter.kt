package astminer.parse.cpp

import astminer.common.*
import astminer.common.model.*

class FuzzyMethodSplitter : TreeMethodSplitter<FuzzyNode> {

    companion object {
        private const val METHOD_NODE = "METHOD"
        private const val METHOD_NAME_NODE = "NAME"
        private const val METHOD_RETURN_NODE = "METHOD_RETURN"
        private const val METHOD_RETURN_TYPE_NODE = "TYPE_FULL_NAME"

        private const val CLASS_DECLARATION_NODE = "TYPE_DECL"
        private const val CLASS_NAME_NODE = "NAME"

        private const val METHOD_PARAMETER_NODE = "METHOD_PARAMETER_IN"
        private const val PARAMETER_NAME_NODE = "NAME"
        private const val PARAMETER_TYPE_NODE = "TYPE_FULL_NAME"
    }

    override fun splitIntoMethods(root: FuzzyNode): Collection<MethodInfo<FuzzyNode>> {
        val methodRoots = root.preOrder().filter { it.getTypeLabel() == METHOD_NODE }
        return methodRoots.map { collectMethodInfo(it as FuzzyNode) }
    }

    private fun collectMethodInfo(methodNode: FuzzyNode): MethodInfo<FuzzyNode> {
        val methodReturnType =
                methodNode.getChildOfType(METHOD_RETURN_NODE)?.getChildOfType(METHOD_RETURN_TYPE_NODE) as? FuzzyNode
        val methodName = methodNode.getChildOfType(METHOD_NAME_NODE) as? FuzzyNode

        val classRoot = getEnclosingClass(methodNode)
        val className = classRoot?.getChildOfType(CLASS_NAME_NODE) as? FuzzyNode

        val parameters = methodNode.getChildrenOfType(METHOD_PARAMETER_NODE)
        val parameterNodes = parameters.map { node ->
            val fuzzyNode = node as FuzzyNode
            ParameterNode(
                    fuzzyNode,
                    fuzzyNode.getChildOfType(PARAMETER_TYPE_NODE) as? FuzzyNode,
                    fuzzyNode.getChildOfType(PARAMETER_NAME_NODE) as? FuzzyNode
            )
        }.toList()

        return MethodInfo(
                MethodNode(methodNode, methodReturnType, methodName),
                ElementNode(classRoot, className),
                parameterNodes
        )
    }

    private fun getEnclosingClass(node: FuzzyNode): FuzzyNode? {
        if (node.getTypeLabel() == CLASS_DECLARATION_NODE) {
            return node
        }
        val parentNode = node.getParent() as? FuzzyNode
        if (parentNode != null) {
            return getEnclosingClass(parentNode)
        }
        return null
    }
}