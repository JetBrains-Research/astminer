package astminer.storage

import astminer.cli.LabeledParseResult
import astminer.common.model.Node
import astminer.common.model.ParseResult

data class LabellingResult<T : Node>(val root: T, val label: String, val filePath: String)

fun <T : Node> LabeledParseResult<T>.toLabellingResult(filePath: String) =
    LabellingResult(this.root, this.label, filePath)

fun <T : Node> ParseResult<T>.labeledWith(label: String) = this.root?.let { LabellingResult(it, label, this.filePath) }

fun <T : Node> ParseResult<T>.labeledWithFilePath() = this.labeledWith(this.filePath)

fun <T : Node> T.labeledWithFilePath(filePath: String) = LabellingResult(this, filePath, filePath)
