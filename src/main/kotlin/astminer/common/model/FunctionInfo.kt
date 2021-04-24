package astminer.common.model

class MethodInfoPropertyNotImplementedException(propertyName: String) :
    UnsupportedOperationException(
        "The property $propertyName of MethodInfo for this language and parser type is not implemented yet. " +
                "Consider implementing it."
    )

private fun notImplemented(propertyName: String): Nothing = throw MethodInfoPropertyNotImplementedException(propertyName)

interface FunctionInfo<T : Node> {
    val nameNode: T?
        get() = notImplemented("nameNode")
    val name: String?
        get() = nameNode?.getToken()
    val root: T
        get() = notImplemented("root")
    val annotations: List<String>
        get() = notImplemented("annotations")
    val modifiers: List<String>
        get() = notImplemented("modifiers")
    val parameters: List<MethodInfoParameter>
        get() = notImplemented("parameters")
    val returnType: String?
        get() = notImplemented("returnType")
    val returnTypeNode: T?
        get() = notImplemented("returnTypeNode")

    // is null because can be only from a small set like {variableDeclaration, classDeclaration..}
    // for instance it cannot be the root of the tree
    // thats why it is probably called "element" and not "node"
    val enclosingElement: T?
        get() = notImplemented("enclosingNode")
    val enclosingElementName: String?
        get() = notImplemented("enclosingElementName")
    val className: String?
        get() = notImplemented("className")
    val isConstructor: Boolean
        get() = notImplemented("isConstructor")
}

data class MethodInfoParameter(val name: String, val type: String?)

// TODO: should be removed
class DummyFunctionInfo<T : Node> : FunctionInfo<T>

fun <T : Node> dummyMethodInfos() = listOf(DummyFunctionInfo<T>())
