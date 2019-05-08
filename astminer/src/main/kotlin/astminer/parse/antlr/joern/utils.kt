package astminer.parse.antlr.joern

import java.io.File
import java.util.concurrent.TimeUnit

private fun String.runCommand() {
    ProcessBuilder(*split(" ").toTypedArray())
            .directory(File(System.getProperty("user.dir")))
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
}

internal fun runJoern(pathToDirectory: String) = "scripts/joern/run.sh $pathToDirectory".runCommand()

internal fun cleanJoern() = "scripts/joern/clean.sh".runCommand()

internal fun setupJoern() = "scripts/joern/setup.sh".runCommand()
