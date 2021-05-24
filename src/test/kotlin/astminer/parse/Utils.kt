package astminer.parse

import astminer.common.model.Node
import astminer.common.model.Parser
import java.io.File

fun <T : Node> Parser<T>.parseFiles(files: List<File>) = files.map { parseFile(it).root }
