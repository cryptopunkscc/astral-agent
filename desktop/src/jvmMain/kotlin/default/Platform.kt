package default

import core.ProcessInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.nio.file.WatchService
import kotlin.coroutines.CoroutineContext

abstract class Platform(
    override val coroutineContext: CoroutineContext
) : core.Platform {

    override val userHome: File get() = File(System.getProperty("user.home"))

    override fun File.observeFileChanges(): Flow<Long> = channelFlow {
        Thread {
            FileSystems.getDefault().newWatchService().use { watchService: WatchService ->
                val watchKey: WatchKey = FileSystems.getDefault()
                    .getPath(parentFile.absolutePath)
                    .register(watchService, arrayOf(StandardWatchEventKinds.ENTRY_MODIFY))

                while (!channel.isClosedForSend) {
                    for (event in watchKey.pollEvents()) {
                        val sent = trySend(lastModified())
                        if (sent.isFailure)
                            break
                    }

                    if (!watchKey.reset())
                        channel.close()
                }
            }
        }.start()

        awaitClose()
    }.distinctUntilChanged()

    override fun File.exec(env: Map<String, String>): Process = absolutePath.exec(emptyMap())

    override fun String.exec(env: Map<String, String>): Process = split(' ', '\n').exec(env)

    override fun List<String>.exec(env: Map<String, String>): Process = ProcessBuilder(this).apply {
        environment().putAll(env)
    }.start()

    override fun Process.readText(): String = inputStream.reader().useLines { it.firstOrNull().orEmpty() }

    override fun Process.lines(): Flow<String> = channelFlow {
        launch {
            errorStream.reader().readText().let { text ->
                if (text.isNotBlank()) {
                    val message = info().command().map { "$it: $text" }.orElse(text)
                    throw IOException(message)
                }
            }
        }
        launch {
            inputStream.reader().useLines { lines ->
                lines.forEach { line ->
                    send(line)
                }
            }
            channel.close()
        }
        awaitClose {
            if (isAlive)
                sigint()
                    .waitFor()
        }
    }.buffer(512)

    override fun Process.sysOut(): Process = apply { launch { inputStream.copyTo(System.out) } }

    override fun Process.sysErr(): Process = apply { launch { errorStream.copyTo(System.err) } }

    abstract override fun Long.sigint(): Process

    abstract override fun processInfo(name: String): List<ProcessInfo>

    companion object {
        val OS_NAME: String get() = System.getProperty("os.name")
    }
}
