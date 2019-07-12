package astminer.compare

import astminer.common.Node
import astminer.common.storage.writeLinesToFile
import java.io.File

data class ParsedTree(val parserName : String, val tree: Node, val fileName : String, val numberOfLines: Int)

// check for unique? set full name?
fun Any.name() : String {
    return this::class.java.simpleName
}

class TreeFeatureValueStorage(private val separator: String) {

    private val parsedTrees: MutableList<ParsedTree> = ArrayList()
    private val features: MutableSet<TreeFeature<out Any>> = HashSet()

    private val idField = Field("id") { parsedTrees.indexOf(it).toString() }
    private val parserField = Field("parser_name") { it.parserName }
    private val fileNameField = Field("file_name") { it.fileName }
    private val NOLField = Field("number_of_lines") { it.numberOfLines.toString() }

    private val fields: List<Field> = listOf(idField, parserField, fileNameField, NOLField)

    data class Field(val header: String, val value: (ParsedTree) -> String)

    fun storeFeature(feature : TreeFeature<out Any>) {
        features.add(feature)
    }

    fun storeFeatures(features: List<TreeFeature<out Any>>) {
        this.features.addAll(features)
    }

    fun storeParsedTree(parsedTree: ParsedTree) {
        parsedTrees.add(parsedTree)
    }

    fun save(directoryPath: String) {
        File(directoryPath).mkdirs()
        val file = File("$directoryPath/features.csv")

        val lines = ArrayList<String>()

        val csvHeaders = fields.map { it.header }.joinToString(separator = separator)
        lines.add(features.map { it.name() }.fold(csvHeaders) { c, f -> "$c$separator$f" } )

        parsedTrees.forEach { t ->
            val csvFields = fields.map { it.value(t) }.joinToString(separator = separator)
            lines.add(features.map { it.findAsString(t.tree) }.fold(csvFields) { c, f -> "$c$separator$f" } )
        }

        writeLinesToFile(lines, file)
    }
}
