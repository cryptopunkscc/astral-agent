import compose.closeDetachedAstrald
import java.io.File

class Astrald(
    platform: Platform
) : Platform by platform {
    suspend fun shouldStart(): Boolean =
        detachedAstraldProcesses().let { list ->
            list.isEmpty() || closeDetachedAstrald(list) { selected ->
                selected.tryClose()
            }.await()
        }

    fun start(): Process = astraldExecutable
        .apply { copyAstraldIfNeeded() }
        .exec()
        .sysOut().sysErr()

    private fun detachedAstraldProcesses(): List<ProcessInfo> = processInfo("astrald")

    private fun Iterable<ProcessInfo>.tryClose(): List<ProcessInfo> {
        forEach { process -> process.sigint() }
        Thread.sleep(1000)
        return detachedAstraldProcesses()
    }

    private fun File.copyAstraldIfNeeded() {
        if (!exists()) astraldEmbedded
            .copyTo(this)
            .setExecutable(true, true)
    }
    private val astraldEmbedded get() = composeResourcesDir.resolve("astrald")
}
