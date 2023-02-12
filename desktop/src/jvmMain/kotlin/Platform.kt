import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import java.io.File

interface Platform : CoroutineScope {

    // File
    val userHome: File
    val astraldExecutable: File
    fun File.observeFileChanges(): Flow<Long>

    // Process
    fun File.exec(): Process
    fun String.exec(): Process
    fun Array<String>.exec(): Process
    fun Process.readText(): String
    fun Process.lines(): Flow<String>
    fun Process.sysOut(): Process
    fun Process.sysErr(): Process
    fun processInfo(name: String): List<ProcessInfo>
    fun Long.sigint(): Process

    // Defaults
    fun Process.sigint(): Process = pid().sigint()
    fun ProcessInfo.sigint(): Process = pid.sigint()

    object Empty : Platform {
        override val coroutineContext = SupervisorJob()
        override val userHome: File get() = throw NotImplementedError()
        override val astraldExecutable: File get() = throw NotImplementedError()
        override fun File.observeFileChanges(): Flow<Long> = throw NotImplementedError()
        override fun File.exec(): Process = throw NotImplementedError()
        override fun String.exec(): Process = throw NotImplementedError()
        override fun Array<String>.exec(): Process = throw NotImplementedError()
        override fun Process.readText(): String = throw NotImplementedError()
        override fun Process.lines(): Flow<String> = throw NotImplementedError()
        override fun Process.sysOut(): Process = throw NotImplementedError()
        override fun Process.sysErr(): Process = throw NotImplementedError()
        override fun processInfo(name: String): List<ProcessInfo> = throw NotImplementedError()
        override fun Long.sigint(): Process = throw NotImplementedError()
    }
}

data class ProcessInfo(
    val user: String,
    val pid: Long,
    val command: List<String>,
)
