package astminer.parse

import astminer.common.model.Node

inline fun <reified T: Node> T.findEnclosingElementBy(condition: (T) -> Boolean): T? {
    var curNode = this.getParent()
    while (!(curNode == null || condition(curNode as T))) {
        curNode = curNode.getParent()
    }
    return curNode as T?
}
