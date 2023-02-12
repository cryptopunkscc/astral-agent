@file:JvmName("Astral Agent")

import gnome.GnomeAutostart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import nix.NixPlatform
import kotlin.system.exitProcess

fun main() {
    val coroutineContext = SupervisorJob() + Dispatchers.IO
    val platform = NixPlatform(coroutineContext)
    Application(
        platform = NixPlatform(coroutineContext),
        autostart = GnomeAutostart(platform)
    ).runBlocking()
    exitProcess(0)
}
