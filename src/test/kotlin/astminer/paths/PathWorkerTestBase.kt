package astminer.paths

import astminer.common.model.Node
import astminer.common.postOrder
import org.junit.Assert
import org.junit.Test

abstract class PathWorkerTestBase {
    abstract fun getTree(): Node

    @Test
    fun anyPathsReturned() {
        val allPaths = PathWorker().retrievePaths(getTree())
        Assert.assertFalse("At least some paths should be retrieved from a non-trivial tree", allPaths.isEmpty())
    }

    @Test
    fun pathsCountNoLimit() {
        val tree = getTree()
        val nLeaves = tree.postOrder().count { it.isLeaf() }

        val allPaths = PathWorker().retrievePaths(tree)
        val expectedCount = (nLeaves * (nLeaves - 1)) / 2

        Assert.assertEquals("A tree with $nLeaves leaves contains $expectedCount paths, " +
                "one per distinct ordered pair of leaves. Worker returned ${allPaths.size}",
                expectedCount, allPaths.size)
    }

    @Test
    fun pathValidity() {
        val tree = getTree()

        val allPaths = PathWorker().retrievePaths(tree)
        allPaths.forEach {
            assertPathIsValid(it)
        }
    }

    @Test
    fun countsForAllLimitCombinations() {
        val maxLengthLimit = 100

        val tree = getTree()
        val leavesCount = tree.postOrder().count { it.isLeaf() }
        val allPathCharacteristics = getAllPathCharacteristics(tree)

        for (maxLength in 1..maxLengthLimit) {
            for (maxWidth in 1..leavesCount) {
                val paths = PathWorker().retrievePaths(tree, maxLength, maxWidth)
                Assert.assertEquals(
                        "Unexpected paths count with length $maxLength and width $maxWidth",
                        allPathCharacteristics.count { (w, len) -> w <= maxWidth && len <= maxLength },
                        paths.size
                )
            }
        }
    }

    @Test
    fun validityForAllLimitCombinations() {
        val maxLengthLimit = 100

        val tree = getTree()
        val leavesCount = tree.postOrder().count { it.isLeaf() }

        for (maxLength in 1..maxLengthLimit) {
            for (maxWidth in 1..leavesCount) {
                val paths = PathWorker().retrievePaths(tree, maxLength, maxWidth)
                paths.forEach { assertPathIsValid(it) }
            }
        }
    }
}