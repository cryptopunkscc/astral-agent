import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

fun shouldStartAstrald(): Boolean {
    val processes = detachedAstraldProcesses()
    return processes.isEmpty() || killDetachedAstrald(mutableStateOf(processes)) { selected ->
        runBlocking { selected.killDetached() }
    }
}

fun detachedAstraldProcesses(): List<ProcessInfo> = processInfo("astrald")

suspend fun Iterable<ProcessInfo>.killDetached(): List<ProcessInfo> {
    forEach { process -> process.sigint() }
    delay(1000)
    return detachedAstraldProcesses()
}

fun startAstrald(): Process = astraldExecutable
    .apply { copyAstraldIfNeeded() }
    .exec()
    .sysOut().sysErr()

private fun File.copyAstraldIfNeeded() {
    if (!exists()) astraldEmbedded
        .copyTo(this)
        .setExecutable(true, true)
}

private val astraldExecutable get() = File(System.getProperty("user.home")).resolve(".local/bin/astrald")
private val astraldEmbedded get() = composeResourcesDir.resolve("astrald")
