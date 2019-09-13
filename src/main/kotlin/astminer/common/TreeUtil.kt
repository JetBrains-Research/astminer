package astminer.common

import java.util.ArrayList


fun Node.postOrderIterator(): Iterator<Node> {
    //TODO implement properly
    return postOrder().listIterator()
}

fun Node.preOrderIterator(): Iterator<Node> {
    return preOrder().listIterator()
}

fun doTraversePostOrder(node: Node, resultList: MutableList<Node>) {
    node.getChildren().forEach { doTraversePostOrder(it, resultList) }
    resultList.add(node)
}

fun doTraversePreOrder(node: Node, resultList: MutableList<Node>) {
    resultList.add(node)
    node.getChildren().forEach { doTraversePreOrder(it, resultList) }
}

fun Node.postOrder(): List<Node> {
    val result: MutableList<Node> = ArrayList()
    doTraversePostOrder(this, result)
    return result
}

fun Node.preOrder(): List<Node> {
    val result: MutableList<Node> = ArrayList()
    doTraversePreOrder(this, result)
    return result
}

fun normalizeTokenInNode(node: Node, defaultToken: String) {
    val token = node.getToken()
    node.setToken(normalizeToken(token, defaultToken))
}

/**
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 */
fun normalizeToken(token: String, defaultToken: String): String {
    val cleanToken = token.toLowerCase()
            .replace("\\\\n".toRegex(), "") // escaped new line
            .replace("//s+".toRegex(), "") // whitespaces
            .replace("[\"',]".toRegex(), "") // quotes, apostrophies, commas
            .replace("\\P{Print}".toRegex(), "") // unicode weird characters

    val stripped = cleanToken.replace("[^A-Za-z]".toRegex(), "")

    return if (stripped.isEmpty()) {
        val carefulStripped = cleanToken.replace(" ", "_")
        if (carefulStripped.isEmpty()) {
            defaultToken
        } else {
            carefulStripped
        }
    } else {
        stripped
    }
}

/**
 * The function was adopted from the original code2vec implementation in order to match their behavior:
 * https://github.com/tech-srl/code2vec/blob/master/JavaExtractor/JPredict/src/main/java/JavaExtractor/Common/Common.java
 */
fun splitToSubtokens(token: String) = token
        .trim()
        .split("(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+".toRegex())
        .map { s -> normalizeToken(s, "") }
        .filter { it.isNotEmpty() }
        .toList()
