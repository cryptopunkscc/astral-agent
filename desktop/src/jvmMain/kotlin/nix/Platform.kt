package nix

import core.ProcessInfo
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.coroutines.CoroutineContext

class Platform(
    coroutineContext: CoroutineContext
) : default.Platform(coroutineContext) {

    override val astraldExecutable: File get() = userHome.resolve(".local/bin/astrald")

    override fun Long.sigint(): Process = "kill -s SIGINT $this".exec()

    override fun processInfo(name: String): List<ProcessInfo> = runBlocking {
        arrayOf("/bin/bash", "-c", "ps aux | grep $name").exec().lines().toList().run {
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
