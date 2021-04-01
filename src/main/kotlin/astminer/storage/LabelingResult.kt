package astminer.storage

import astminer.cli.LabeledParseResult
import astminer.common.model.Node
import astminer.common.model.ParseResult

/**
 * An AST subtree with a label and the path of the source file.
 * @property root The root of the AST subtree.
 * @property label Any label for this subtree.
 * @property filePath The path to the source file where the AST is from.
 */
data class LabellingResult<T : Node>(val root: T, val label: String, val filePath: String)

fun <T : Node> LabeledParseResult<T>.toLabellingResult(filePath: String) =
    LabellingResult(this.root, this.label, filePath)

fun <T : Node> ParseResult<T>.labeledWith(label: String) = this.root?.let { LabellingResult(it, label, this.filePath) }

fun <T : Node> ParseResult<T>.labeledWithFilePath() = this.labeledWith(this.filePath)

fun <T : Node> T.labeledWithFilePath(filePath: String) = LabellingResult(this, filePath, filePath)
