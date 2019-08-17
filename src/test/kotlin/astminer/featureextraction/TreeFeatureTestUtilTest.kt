package astminer.featureextraction

import org.junit.Assert
import org.junit.Test

class TreeFeatureTestUtilTest {

    @Test
    fun testRestoreFromPrettyPrint() {
        val node0 = PrettyNode("0", "a")
        val node1 = PrettyNode("1", "b")
        val node2 = PrettyNode("2", "c")
        val node3 = PrettyNode("3", "d")
        val node4 = PrettyNode("4", "e")
        val node5 = PrettyNode("5", "f")
        val node6 = PrettyNode("6", "g")
        val node7 = PrettyNode("7", "h")

        node1.setParent(node0)
        node2.setParent(node0)
        node3.setParent(node0)
        node4.setParent(node1)
        node5.setParent(node4)
        node6.setParent(node1)
        node7.setParent(node3)

        val prettyTree = node0.toPrettyString()
        val restoredTree = restoreFromPrettyPrint(prettyTree)!!
        val prettyRestoredTree = restoredTree.toPrettyString()

        Assert.assertEquals(prettyTree, prettyRestoredTree)
    }
}