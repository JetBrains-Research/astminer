package astminer.parse.cpp

import astminer.examples.forFilesWithSuffix
import org.junit.Assert
import org.junit.Test
import java.io.File

class FuzzyCppParserTest {

    @Test
    fun testNodeIsNotNull() {
        val parser = FuzzyCppParser()
        val fileName = "testData/fuzzy/test.cpp"

        val nodes = parser.parse(arrayListOf(fileName))
        Assert.assertTrue("Parse tree for a valid file should not be null", nodes.size == 1 && nodes[0] != null)
    }

    @Test
    fun testInputStreamParsing() {
        val folder = "testData/fuzzy/"
        val nodes = ArrayList<FuzzyNode>()
        var n = 0
        val parser = FuzzyCppParser()
        File(folder).forFilesWithSuffix(".cpp") { file ->
            n++
            parser.parse(file.inputStream())?.let { nodes.add(it) }
        }
        Assert.assertEquals(n, nodes.size)
    }

    @Test
    fun testProjectParsing() {
        val folder = "testData/fuzzy/"
        val parser = FuzzyCppParser()
        val nodes = parser.parseProject(File(folder)) { it.walkTopDown().filter { it.isFile }.toList() }
        Assert.assertEquals("There is only 3 file with .py extension in 'testData/examples' folder",3, nodes.filterNotNull().size)
    }

    @Test
    fun testPreprocessing() {
        val folder = "testData/fuzzy"
        val includeFile = "preprocIncludeTest.cpp"
        val defineFile = "preprocDefineTest.cpp"
        val parser = FuzzyCppParser()

        parser.preprocessWithoutIncludes(File("$folder/$includeFile"))
        parser.preprocessWithoutIncludes(File("$folder/$defineFile"))

        Assert.assertEquals("'include' directives should not be replaced", File("$folder/$includeFile").readInOneLine(), File("$folder/${parser.preprocessDirName}/$includeFile").readInOneLine())
        Assert.assertEquals("'define' directives should be replaced", "for (int i = (0); i < (10); ++i) { }", File("$folder/${parser.preprocessDirName}/$defineFile").readInOneLine())
    }

    @Test
    fun testPreprocessingAndParsing() {
        val folder = "testData/fuzzy"
        val fileName = "test.cpp"
        val parser = FuzzyCppParser()
        parser.preprocessWithoutIncludes(File("$folder/$fileName"))
        val nodes = parser.parse(arrayListOf("$folder/${parser.preprocessDirName}/$fileName"))
        File("$folder/${parser.preprocessDirName}").deleteRecursively()
        Assert.assertTrue("Parse tree for a valid file should not be null", nodes.size == 1 && nodes[0] != null)
    }
}