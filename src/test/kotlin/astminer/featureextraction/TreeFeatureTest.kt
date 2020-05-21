package astminer.featureextraction

import org.junit.Assert
import org.junit.Test
import java.io.File

class TreeFeatureTest {

    @Test
    fun testDepthFeature() {
        val printedTree = File("src/test/resources/featureextraction/prettyTree.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        Assert.assertEquals(4, Depth.compute(tree))
    }

    @Test
    fun testNumberOfNodes() {
        val printedTree = File("src/test/resources/featureextraction/prettyTree.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        Assert.assertEquals(12, NumberOfNodes.compute(tree))
    }

    @Test
    fun testBranchingFactorOfLeaf() {
        val printedTree = File("src/test/resources/featureextraction/prettyLeaf.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        Assert.assertTrue(BranchingFactor.compute(tree) == 0.0)
    }

    @Test
    fun testBranchingFactor() {
        val printedTree = File("src/test/resources/featureextraction/prettyTree_bf.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        Assert.assertTrue(BranchingFactor.compute(tree) == 2.0)
    }

    @Test
    fun testCompressiblePathLengthsInLeaf() {
        val printedTree = File("src/test/resources/featureextraction/prettyLeaf.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        Assert.assertEquals(listOf<Int>(), CompressiblePathLengths.compute(tree))
    }

    @Test
    fun testCompressiblePathLengths() {
        val printedTree = File("src/test/resources/featureextraction/prettyTree_paths.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        val expected = listOf(4, 1, 2, 2, 4, 4, 5).sorted()
        Assert.assertEquals(expected, CompressiblePathLengths.compute(tree).sorted())
    }

    @Test
    fun testNodeTypes() {
        val printedTree = File("src/test/resources/featureextraction/prettyTree.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        val expected = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11").sorted()
        Assert.assertEquals(expected, NodeTypes.compute(tree).sorted())
    }

    @Test
    fun testTokens() {
        val printedTree = File("src/test/resources/featureextraction/prettyTree.txt").readText()
        val tree: PrettyNode = restoreFromPrettyPrint(printedTree)!!
        val expected = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l").sorted()
        Assert.assertEquals(expected, Tokens.compute(tree).sorted())
    }

}