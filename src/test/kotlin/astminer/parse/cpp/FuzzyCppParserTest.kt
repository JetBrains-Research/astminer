package astminer.parse.cpp

import astminer.common.getProjectFilesWithExtension
import astminer.examples.forFilesWithSuffix
import org.junit.Assert
import org.junit.Test
import java.io.File

class FuzzyCppParserTest {

    @Test
    fun testNodeIsNotNull() {
        val parser = FuzzyCppParser()
        val file = File("src/test/resources/fuzzy/test.cpp")

        val nodes = parser.parseFile(file)
        Assert.assertTrue("Parse tree for a valid file should not be null", nodes.root != null)
    }

    @Test
    fun testInputStreamParsing() {
        val folder = File("src/test/resources/fuzzy/")
        val nodes = ArrayList<FuzzyNode>()
        var n = 0
        val parser = FuzzyCppParser()
        folder.forFilesWithSuffix(".cpp") { file ->
            n++
            parser.parseInputStream(file.inputStream())?.let { nodes.add(it) }
        }
        Assert.assertEquals(n, nodes.size)
    }

    @Test
    fun testProjectParsing() {
        val folder = File("src/test/resources/fuzzy/")
        val parser = FuzzyCppParser()
        val nodes = mutableListOf<FuzzyNode?>()
        parser.parseFiles(getProjectFilesWithExtension(folder, "cpp")) {
            nodes.add(it.root)
        }
        Assert.assertEquals(
                "There is only 3 file with .cpp extension in 'testData/examples' folder",
                3,
                nodes.filterNotNull().size
        )
    }

    @Test
    fun testPreprocessingDefine() {
        val folder = File("src/test/resources/fuzzy")
        val preprocessedFolder = folder.resolve("preprocessed")
        preprocessedFolder.mkdir()
        val defineFileName = "preprocDefineTest.cpp"
        val parser = FuzzyCppParser()

        parser.preprocessFile(folder.resolve(defineFileName), preprocessedFolder)

        Assert.assertEquals(
                "'define' directives should be replaced",
                "for (int i = (0); i < (10); ++i) { }",
                preprocessedFolder.resolve(defineFileName).readInOneLine()
        )
        preprocessedFolder.deleteRecursively()
    }

    @Test
    fun testPreprocessingInclude() {
        val folder = File("src/test/resources/fuzzy")
        val preprocessedFolder = folder.resolve("preprocessed")
        preprocessedFolder.mkdir()
        val includeFileName = "preprocIncludeTest.cpp"
        val parser = FuzzyCppParser()

        parser.preprocessFile(folder.resolve(includeFileName), preprocessedFolder)

        Assert.assertEquals(
                "'include' directives should not be replaced",
                folder.resolve(includeFileName).readInOneLine(),
                preprocessedFolder.resolve(includeFileName).readInOneLine()
        )
        preprocessedFolder.deleteRecursively()
    }

    @Test
    fun testPreprocessingProject() {
        val projectRoot = File("src/test/resources/examples/cpp")
        val preprocessedRoot = File("src/test/resources/examples/preprocessed")
        preprocessedRoot.mkdir()
        val parser = FuzzyCppParser()

        parser.preprocessProject(projectRoot, preprocessedRoot)
        val nodes = mutableListOf<FuzzyNode?>()
        parser.parseFiles(getProjectFilesWithExtension(projectRoot, "cpp")) {
            nodes.add(it.root)
        }

        Assert.assertEquals(
                "Parse tree for a valid file should not be null. There are 5 files in example project.",
                5,
                nodes.filterNotNull().size
        )
        preprocessedRoot.deleteRecursively()
    }
}