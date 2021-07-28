package astminer.parse.cpp

import astminer.checkExecutable
import astminer.common.forFilesWithSuffix
import astminer.common.getProjectFilesWithExtension
import astminer.parse.fuzzy.FuzzyNode
import astminer.parse.fuzzy.FuzzyParsingResultFactory
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parseFiles
import org.junit.Assert
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.io.File

class FuzzyCppParserTest {

    @Before
    fun checkGPP() = Assume.assumeTrue(checkExecutable("g++"))

    @Test
    fun testInputStreamParsing() {
        val folder = File("src/test/resources/fuzzy/")
        val nodes = ArrayList<FuzzyNode>()
        var n = 0
        val parser = FuzzyCppParser()
        folder.forFilesWithSuffix(".cpp") { file ->
            n++
            parser.parseInputStream(file.inputStream()).let { nodes.add(it) }
        }
        Assert.assertEquals(n, nodes.size)
    }

    @Test
    fun testProjectParsing() {
        val folder = File("src/test/resources/fuzzy/")
        val parser = FuzzyCppParser()
        val nodes = parser.parseFiles(getProjectFilesWithExtension(folder, "cpp"))
        Assert.assertEquals(
            "There is only 3 file with .cpp extension in 'testData/examples' folder",
            3,
            nodes.size
        )
    }

    @Test
    fun testPreprocessingDefine() {
        val folder = File("src/test/resources/fuzzy")
        val defineFileName = "preprocDefineTest.cpp"
        val preprocessedFileName = "preprocDefineTest_preprocessed.cpp"

        FuzzyParsingResultFactory.preprocess(folder.resolve(defineFileName))

        Assert.assertEquals(
            "'define' directives should be replaced",
            "for (int i = (0); i < (10); ++i) { }",
            folder.resolve(preprocessedFileName).readInOneLine()
        )
        folder.resolve(preprocessedFileName).delete()
    }

    @Test
    fun testPreprocessingInclude() {
        val folder = File("src/test/resources/fuzzy")
        val includeFileName = "preprocIncludeTest.cpp"
        val preprocessedFileName = "preprocIncludeTest_preprocessed.cpp"

        FuzzyParsingResultFactory.preprocess(folder.resolve(includeFileName))

        Assert.assertEquals(
            "'include' directives should not be replaced",
            folder.resolve(includeFileName).readInOneLine(),
            folder.resolve(preprocessedFileName).readInOneLine()
        )
        folder.resolve(preprocessedFileName).delete()
    }

    @Test
    fun testPreprocessingProject() {
        val projectRoot = File("src/test/resources/examples/cpp")

        val files = getProjectFilesWithExtension(projectRoot, "cpp")
        val nodes = FuzzyParsingResultFactory.parseFiles(files) { it.root }.filterNotNull()

        Assert.assertEquals(
            "Parse tree for a valid file should not be null. There are 5 files in example project.",
            5,
            nodes.size
        )
        files.map { "${it.nameWithoutExtension}_preprocessed.${it.extension}" }
            .forEach { projectRoot.resolve(it).delete() }
    }
}
