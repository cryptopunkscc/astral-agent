import compose.killDetachedAstrald
import java.io.File

object Astrald {
    fun shouldStart(): Boolean =
        detachedAstraldProcesses().run {
            isEmpty() || killDetachedAstrald { selected ->
                selected.tryKill()
            }
        }

    fun start(): Process = astraldExecutable
        .apply { copyAstraldIfNeeded() }
        .exec()
        .sysOut().sysErr()
}

private fun detachedAstraldProcesses(): List<ProcessInfo> = processInfo("astrald")

private fun Iterable<ProcessInfo>.tryKill(): List<ProcessInfo> {
    forEach { process -> process.sigint() }
    Thread.sleep(1000)
    return detachedAstraldProcesses()
}

private fun File.copyAstraldIfNeeded() {
    if (!exists()) astraldEmbedded
        .copyTo(this)
        .setExecutable(true, true)
}

private val astraldExecutable get() = userHome.resolve(".local/bin/astrald")
private val astraldEmbedded get() = composeResourcesDir.resolve("astrald")
