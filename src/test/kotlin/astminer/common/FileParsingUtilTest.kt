package astminer.common

import org.junit.Assert
import org.junit.Test
import java.io.File

class FileParsingUtilTest {

    @Test
    fun testNumberOfLines() {
        val file = File("src/test/resources/common/ParsableFile.java")
        val nol = numberOfLines(file)
        Assert.assertEquals("All non-empty lines including comments should be counted", nol, 8)
    }

    @Test
    fun testChangeExtensionTo() {
        var file = File("src/test/resources/common/FileToChangeExtension.csv")
        val newExtension = "txt"
        val text = "Lorem ipsum dolor sit amet"

        file.writeText(text)
        file = changeExtensionTo(file, newExtension)

        Assert.assertTrue("File extension should be changed but its content should not", file.extension == newExtension && file.readText() == text)

        file.delete()
    }

    @Test
    fun testAddClassWrapper() {
        val file = File("src/test/resources/common/FileToAddClassWrapper.java")
        val text = "public static void foo() { }"

        file.writeText(text)
        addClassWrapper(file, "Foo")

        Assert.assertEquals("File wrapper should be added with braces and newlines", file.readText(), "class Foo {\n$text\n}")

        file.delete()
    }

    @Test
    fun testHasNoSyntaxErrors() {
        val file = File("src/test/resources/common/ParsableFile.java")
        Assert.assertFalse("This file doesn't have any syntax errors", hasSyntaxErrors(file))
    }

    @Test
    fun testHasSyntaxErrors() {
        val file = File("src/test/resources/common/NonParsableFile.java")
        Assert.assertTrue("This file has syntax errors", hasSyntaxErrors(file))
    }

}