import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.nio.file.*

val userHome get() = File(System.getProperty("user.home"))

fun File.exec() = absolutePath.exec()
fun String.exec(): Process = Runtime.getRuntime().exec(this)
fun Array<String>.exec(): Process = Runtime.getRuntime().exec(this)

fun Process.string(): String = inputStream.reader().useLines { it.first() }
fun Process.flow(): Flow<String> = channelFlow {
    invokeOnClose {
        if (isAlive)
            sigint()
    }
    launch {
        errorStream.reader().readText().let { text ->
            if (text.isNotBlank()) {
                val message = info().command().map { "$it: $text" }.orElse(text)
                throw IOException(message)
            }

        }
    }
    inputStream.reader().useLines { lines ->
        lines.forEach { line ->
            trySend(line)
        }
    }
}.buffer(512)

fun Process.sysOut() = apply { scope.launch { inputStream.copyTo(System.out) } }
fun Process.sysErr() = apply { scope.launch { errorStream.copyTo(System.err) } }

fun Process.sigint(): Process = pid().sigint().sysOut().sysErr()
fun Long.sigint() = "kill -s SIGINT $this".exec()

fun processInfo(name: String): List<ProcessInfo> {
    return runBlocking {
        arrayOf("/bin/bash", "-c", "ps aux | grep $name").exec().flow().toList().run {
            when {
                size < 3 -> emptyList()
                else -> {
                    dropLast(2).mapNotNull { line ->
                        runCatching {
                            val chunks = line.split(" ").filter(String::isNotBlank)
                            val user = chunks[0]
                            val pid = chunks[1].toLongOrNull()
                            val command = chunks.drop(10)
                            if (pid == null) null
                            else ProcessInfo(
                                user = user,
                                pid = pid,
                                command = command,
                            )
                        }.onFailure {
                            it.printStackTrace()
                        }.getOrNull()
                    }
                }
            }
        }
    }
}

data class ProcessInfo(
    val user: String,
    val pid: Long,
    val command: List<String>,
)

fun ProcessInfo.sigint() = pid.sigint()

fun File.observeFileChanges(): Flow<Long> = channelFlow {
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
