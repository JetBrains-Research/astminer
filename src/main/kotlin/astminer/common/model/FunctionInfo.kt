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
    val parseResult: ParseResult<T>
        get() = notImplemented("parseResult")
    val root: T
        get() = parseResult.root
    val filePath: String
        get() = parseResult.filePath
    val annotations: List<String>
        get() = notImplemented("annotations")
    val modifiers: List<String>
        get() = notImplemented("modifiers")
    val parameters: List<MethodInfoParameter>
        get() = notImplemented("parameters")
    val returnType: String?
        get() = notImplemented("returnType")
    val enclosingElement: EnclosingElement<T>?
        get() = notImplemented("enclosingElement")
    val isConstructor: Boolean
        get() = notImplemented("isConstructor")
}

data class MethodInfoParameter(val name: String, val type: String?)

data class EnclosingElement<T>(val type: EnclosingElementType, val name: String?, val root: T)

enum class EnclosingElementType {
    Class,
    Function,
    Method,
    VariableDeclaration,
}

// TODO: should be removed
class DummyFunctionInfo<T : Node> : FunctionInfo<T>

fun <T : Node> dummyMethodInfos() = listOf(DummyFunctionInfo<T>())
