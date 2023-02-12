package gnome

import core.Platform
import core.Resources
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class Autostart(
    platform: Platform,
    resources: Resources,
) : core.Autostart,
    Platform by platform,
    Resources by resources {

    private val file get() = userHome.resolve(".config").resolve(DESKTOP_FILE_PATH)

    override val enabled: Boolean get() = file.run { exists() && OPTION + true in readText() }

    override suspend fun collect(collector: FlowCollector<Boolean>) = file
        .observeFileChanges()
        .onEach { delay(200) }
        .map { enabled }
        .distinctUntilChanged()
        .collect(collector)

    override fun invoke(enable: Boolean) {
        when (enable) {
            true -> when {
                !file.exists() -> freshCopy()
                else -> set(true)
            }

            false -> when {
                file.exists() -> set(false)
            }
        }
    }

    private fun freshCopy(): Boolean =
        runCatching {
            jarResourceStream(DESKTOP_FILE_PATH)!!.use { input ->
                file.outputStream().use(input::copyTo)
            }
        }.onFailure {
            it.printStackTrace()
        }.isSuccess

    private fun set(checked: Boolean) {
        file.readLines().joinToString("\n") { line ->
            if (OPTION !in line) line
            else OPTION + checked
        }.let { changed ->
            file.writeText(changed)
        }
    }
}

private const val DESKTOP_FILE_PATH = "autostart/astral-agent.desktop"
private const val OPTION = "X-GNOME-Autostart-enabled="
