package core

import java.io.File

class Astrald(
    platform: Platform,
    resources: Resources,
) : Platform by platform,
    Resources by resources {

    enum class Exit {
        Success,       // Normal exit
        Help,          // Help was invoked
        NodeError,     // Node reported an error
        Forced,        // User forced shutdown with double SIGINT
        ConfigError,   // An invalid or non-existent config file provided
    }

    fun start(): Process = astraldExecutable
        .apply { copyAstraldIfNeeded() }
        .exec()
        .sysOut().sysErr()

    fun listDetached(): List<ProcessInfo> = processInfo("astrald")

    fun Iterable<ProcessInfo>.tryClose(): List<ProcessInfo> {
        forEach { process -> process.sigint() }
        Thread.sleep(1000)
        return listDetached()
    }

    private fun File.copyAstraldIfNeeded() {
        if (!exists()) astraldEmbedded
            .copyTo(this)
            .setExecutable(true, true)
    }

    private val astraldEmbedded get() = composeResourcesDir.resolve("astrald")
}
