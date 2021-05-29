package astminer.parse.antlr.php

import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode

abstract class ANTLRPHPFunctionInfo(
    override val root: AntlrNode
) : FunctionInfo<AntlrNode> {
}

class ArrowPhpFunctionInfo(root: AntlrNode) : ANTLRPHPFunctionInfo(root) {

}

class SimplePhpFunctionInfo(root: AntlrNode) : ANTLRPHPFunctionInfo(root) {

}