package core

import java.io.File

class Astrald(
    platform: Platform,
    resources: Resources,
) : Platform by platform,
    Resources by resources {

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
