@file:JvmName("Astral Agent")

import core.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.system.exitProcess

fun main() {
    val coroutineContext = SupervisorJob() + Dispatchers.IO
    val platform = nix.Platform(coroutineContext)
    val resources = jvm.Resources(object {})
    val autostart = gnome.Autostart(platform, resources)
    Application(
        platform = platform,
        autostart = autostart,
        resources = resources,
    ).runBlocking()
    exitProcess(0)
}
