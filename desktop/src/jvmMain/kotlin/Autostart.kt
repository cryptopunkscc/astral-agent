import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

object Autostart : Flow<Boolean>, (Boolean) -> Unit {

    private const val desktopFilePath = "autostart/astral-agent.desktop"
    private const val option = "X-GNOME-Autostart-enabled="
    private val file get() = userHome.resolve(".config").resolve(desktopFilePath)

    val enabled get() = file.run { exists() && option + true in readText() }

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
            jarResourceStream(desktopFilePath)!!.use { input ->
                file.outputStream().use(input::copyTo)
            }
        }.onFailure {
            it.printStackTrace()
        }.isSuccess

    private fun set(checked: Boolean) {
        file.readLines().joinToString("\n") { line ->
            if (option !in line) line
            else option + checked
        }.let { changed ->
            file.writeText(changed)
        }
    }
}
