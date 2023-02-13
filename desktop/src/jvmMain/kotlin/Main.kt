@file:JvmName("Astral Agent")

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.system.exitProcess

fun main() {
    val coroutineContext = Root + SupervisorJob() + Dispatchers.IO
    agent(coroutineContext).runBlocking()
    exitProcess(0)
}
