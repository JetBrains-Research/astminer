package astminer

import astminer.config.PipelineConfig
import astminer.pipeline.Pipeline
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
        val config = try {
            yaml.decodeFromString<PipelineConfig>(config.readText())
        } catch (e: SerializationException) {
            logger.error(e) { "Could not read config" }
            println("Could not read config: $e")
            return
        }
        Pipeline(config).run()
    }
}

fun main(args: Array<String>) = PipelineRunner().main(args)
