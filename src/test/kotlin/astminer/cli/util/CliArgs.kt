package astminer.cli.util

import java.io.File

class CliArgs private constructor(val args: List<String>) {

    data class Builder(val testDataDir: File, val extractedDataDir: File) {
        val args = mutableListOf(
            "--project", testDataDir.path,
            "--output", extractedDataDir.path
        )
        fun extensions(extensions: String) = apply {
            args.add("--lang")
            args.add(extensions)
        }

        fun maxPathLength(l: Int) = apply {
            args.add("--maxL")
            args.add(l.toString())
        }

        fun maxPathWidth(w: Int) = apply {
            args.add("--maxW")
            args.add(w.toString())
        }

        fun maxPathContexts(maxPC: Int)= apply {
            args.add("--maxContexts")
            args.add(maxPC.toString())
        }

        fun maxTokens(nTokens: Long) = apply {
            args.add("--maxTokens")
            args.add(nTokens.toString())
        }

        fun maxPaths(nPaths: Long) = apply {
            args.add("--maxPaths")
            args.add(nPaths.toString())
        }

        fun build() = CliArgs(args)
    }
}
