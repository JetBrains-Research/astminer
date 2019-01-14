package miningtool.common

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