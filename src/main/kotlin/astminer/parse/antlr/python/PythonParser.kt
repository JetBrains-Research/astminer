package astminer.parse.antlr.python

import astminer.common.model.Parser
import astminer.parse.ParsingException
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.convertAntlrTree
import me.vovak.antlr.parser.Python3Lexer
import me.vovak.antlr.parser.Python3Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream
import java.lang.Exception

class PythonParser : Parser<AntlrNode> {
    override fun parseInputStream(content: InputStream): AntlrNode {
        return try {
            val lexer = Python3Lexer(CharStreams.fromStream(content))
            lexer.removeErrorListeners()
            val tokens = CommonTokenStream(lexer)
            val parser = Python3Parser(tokens)
            parser.removeErrorListeners()
            val context = parser.file_input()
            convertAntlrTree(context, Python3Parser.ruleNames, Python3Parser.VOCABULARY)
        } catch (e: Exception) {
            throw ParsingException("ANTLR", "Python", e)
        }
    }
}
