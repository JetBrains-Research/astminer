package miningtool.parse.antlr.joern

import me.vovak.antlr.parser.ModuleLexer
import me.vovak.antlr.parser.ModuleParser
import miningtool.common.Parser
import miningtool.parse.antlr.SimpleNode
import miningtool.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream

class CppParser : Parser<SimpleNode> {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = ModuleLexer(CharStreams.fromStream(content))
//        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = ModuleParser(tokens)
//        parser.removeErrorListeners()
        val context = parser.code()
        return convertAntlrTree(context, ModuleParser.ruleNames)
    }
}