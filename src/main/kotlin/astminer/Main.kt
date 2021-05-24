package astminer

import astminer.common.model.FunctionInfoPropertyNotImplementedException
import astminer.config.PipelineConfig
import astminer.pipeline.Pipeline
import astminer.pipeline.ProblemDefinitionException
import com.charleskorn.kaml.PolymorphismStyle
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger("Main")

class PipelineRunner : CliktCommand(name = "") {
    val config: File by argument("config", help = "Path to config").file(
        exists = true,
        folderOkay = false,
        readable = true
    )

    private val yaml = Yaml(configuration = YamlConfiguration(polymorphismStyle = PolymorphismStyle.Property))

    override fun run() {
        try {
            val config = yaml.decodeFromString<PipelineConfig>(config.readText())
            Pipeline(config).run()
        } catch (e: SerializationException) {
            report("Could not read config", e)
            println("\nBe sure to check types of filters and problems for misprints!")
        } catch (e: ProblemDefinitionException) {
            report("Problem is defined incorrectly", e)
        } catch (e: FunctionInfoPropertyNotImplementedException) {
            report("Currently astminer cannot fulfill your request", e)
        }
    }

    private fun report(message: String, e: Exception) {
        logger.error(e) { message }
        println("$message:\n$e")
    }
}

fun main(args: Array<String>) = PipelineRunner().main(args)
