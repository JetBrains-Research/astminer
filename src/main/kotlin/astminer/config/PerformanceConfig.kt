package astminer.config

import kotlinx.serialization.Serializable

private const val DEFAULT_NUM_OF_THREADS = 16

/**
 * Config which defines various performance tweaks
 * (for example number of threads). Can be left blank,
 * then default config will be used.
 * **/
@Serializable
data class PerformanceConfig(
    val numOfThreads: Int
)

val defaultPerformanceConfig = PerformanceConfig(DEFAULT_NUM_OF_THREADS)
