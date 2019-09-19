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
    fun pathsCountWidth1() {
        val tree = getTree()
        val nLeaves = tree.postOrder().count { it.isLeaf() }

        val allPaths = PathWorker().retrievePaths(tree, Int.MAX_VALUE, 1)
        val expectedCount = nLeaves - 1

        Assert.assertEquals("A tree with $nLeaves leaves contains $expectedCount paths of width 1. " +
                "Worker returned ${allPaths.size}",
                expectedCount, allPaths.size)
    }

    @Test
    fun pathsCountAnyWidth() {
        val tree = getTree()
        val nLeaves = tree.postOrder().count { it.isLeaf() }

        for (maxWidth in 1..nLeaves) {
            val paths = PathWorker().retrievePaths(tree, Int.MAX_VALUE, maxWidth)
            val expectedPathsCount = getPathsCountWithNoHeightLimit(nLeaves, maxWidth)
            Assert.assertEquals("A tree with $nLeaves nodes should contain $expectedPathsCount paths " +
                    "of width up to $maxWidth, worker returned ${paths.size}",
                    expectedPathsCount, paths.size)
        }
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
    fun countFunctionsMatch() {
        val tree = getTree()
        val leavesCount = tree.postOrder().count { it.isLeaf() }

        for (maxWidth in 1..leavesCount) {
            Assert.assertEquals(getPathsCountWithNoHeightLimit(leavesCount, maxWidth),
                    countPossiblePaths(tree, Int.MAX_VALUE, maxWidth))
        }
    }

    @Test
    fun countsForAllLimitCombinations() {
        val maxHeightLimit = 100

        val tree = getTree()
        val leavesCount = tree.postOrder().count { it.isLeaf() }

        for (maxHeight in 1..maxHeightLimit) {
            for (maxWidth in 1..leavesCount) {
                val paths = PathWorker().retrievePaths(tree, maxHeight, maxWidth)
                Assert.assertEquals(
                        "Unexpected paths count with height $maxHeight and width $maxWidth",
                        countPossiblePaths(tree, maxHeight, maxWidth),
                        paths.size)
            }
        }
    }

    @Test
    fun validityForAllLimitCombinations() {
        val maxHeightLimit = 100

        val tree = getTree()
        val leavesCount = tree.postOrder().count { it.isLeaf() }

        for (maxHeight in 1..maxHeightLimit) {
            for (maxWidth in 1..leavesCount) {
                val paths = PathWorker().retrievePaths(tree, maxHeight, maxWidth)
                paths.forEach { assertPathIsValid(it) }
            }
        }
    }
}