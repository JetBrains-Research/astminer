package astminer.featureextraction

import astminer.common.model.Node
import astminer.common.storage.writeLinesToFile
import java.io.File

/**
 * Data class for already parsed tree, that contains also additional information about file and parser.
 * @property parserName name of parser that is used for parsing
 * @property tree result of file parsing
 * @property fileName name of parsed file
 * @property numberOfLines number of lines in parsed file
 */
data class ParsedTree(val parserName : String, val tree: Node, val fileName : String, val numberOfLines: Int)

/**
 * Gets simple name of Any.
 */
fun Any.className() : String {
    return this::class.java.simpleName
}

/**
 * Class for store and save [tree features][astminer.featureextraction.TreeFeature] for [parsed trees][astminer.featureextraction.ParsedTree].
 * @property separator separator which is used in resulting file to separate with tree features values
 */
class TreeFeatureValueStorage(private val separator: String) {

    private val parsedTrees: MutableList<ParsedTree> = ArrayList()
    private val features: MutableSet<TreeFeature<Any>> = HashSet()

    private val idField = Field("Id") { parsedTrees.indexOf(it).toString() }
    private val parserField = Field("ParserName") { it.parserName }
    private val fileNameField = Field("FileName") { it.fileName }
    private val NOLField = Field("NumberOfLines") { it.numberOfLines.toString() }
    private val fileName = "features.csv"

    private val fields: List<Field> = listOf(idField, parserField, fileNameField, NOLField)

    /**
     * Data class for additional fields that should be in resulting file with features.
     * @property header name of this field that is used as header
     * @property value function to get value of this field from parsed tree
     */
    data class Field(val header: String, val value: (ParsedTree) -> String)

    /**
     * Stores new tree feature to compute for stored parsed trees.
     * @param feature feature to store
     */
    fun storeFeature(feature : TreeFeature<Any>) {
        features.add(feature)
    }

    /**
     * Stores list of new features to compute for stored parsed trees.
     * @param features list of features to store
     */
    fun storeFeatures(features: List<TreeFeature<Any>>) {
        this.features.addAll(features)
    }

    /**
     * Stores new parsed tree.
     * @param parsedTree parsed tree to store
     */
    fun storeParsedTree(parsedTree: ParsedTree) {
        parsedTrees.add(parsedTree)
    }

    /**
     * Computes all stored features for all stored parsed trees and saves them in a given directory.
     * @param directoryPath path to directory where tree features is saved. If this directory does not exist the new one creates.
     */
    fun save(directoryPath: String) {
        File(directoryPath).mkdirs()
        val file = File("$directoryPath/$fileName")

        val lines = ArrayList<String>()

        val csvHeaders = fields.joinToString(separator = separator) { it.header }
        lines.add(features.map { it.className() }.fold(csvHeaders) { c, f -> "$c$separator$f" } )

        parsedTrees.forEach { t ->
            val csvFields = fields.joinToString(separator = separator) { it.value(t) }
            lines.add(features.map { toCsvString(it.compute(t.tree)) }.fold(csvFields) { c, f -> "$c$separator$f" } )
        }

        writeLinesToFile(lines, file)
    }

    private fun toCsvString(a : Any?) : String {
        if (a is List<*>) {
            return "\"${a.joinToString { toCsvString(it) }.replace("\"","\"\"")}\""
        }
        return a.toString()
    }

}