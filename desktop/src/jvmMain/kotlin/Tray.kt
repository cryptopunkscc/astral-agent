import compose.astralAgentSettings
import dorkbox.systemTray.MenuItem
import dorkbox.systemTray.SystemTray
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.swing.JSeparator

fun astralTray(
    events: MutableSharedFlow<Event>
) : SystemTray {
    SystemTray.AUTO_SIZE = false

    val systemTray: SystemTray = SystemTray.get() ?: throw RuntimeException("Unable to load SystemTray!")

    systemTray.setImage(jarResource("ic_astral_symbolic_white.svg"))

    systemTray.status = "Astral Agent"

    systemTray.menu.add(JSeparator())

    systemTray.menu.add(MenuItem("Settings") {
        astralAgentSettings()
    })

    systemTray.menu.add(JSeparator())

    systemTray.menu.add(MenuItem("Quit") {
        // v4.1
        println("tray quit")
        systemTray.shutdown()
        events.tryEmit(Close(systemTray))
        // v4.2.1 https://github.com/dorkbox/SystemTray/issues/181
//        systemTray.shutdown {
//            events.tryEmit(Close(systemTray))
//        }
    }).shortcut = 'q' // case does not matter

    return systemTray
}
