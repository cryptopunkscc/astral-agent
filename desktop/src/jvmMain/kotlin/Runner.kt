import compose.shouldStart
import core.Application
import core.Close
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun Application.runBlocking(): Unit = runBlocking {

    println("starting tray")
    val tray = astralTray()

    tray.setEnabled(false)
    if (astrald.shouldStart()) {
        val astraldProcess = astrald.start()
        finalizers.add {
            val code = astraldProcess.sigint().sysOut().sysErr().waitFor()
            println("astrald sigint $code")
        }
        launch(Dispatchers.IO) {
            astraldProcess.waitFor()
            events.emit(Close(astraldProcess))
        }
        tray.status = "Astral connected"
    } else {
        tray.status = "Astral detached"
    }

    tray.setEnabled(true)
    events.filterIsInstance<Close>().first()
    println("received close event")
    for (finalize in finalizers) finalize()
    println("finalized")
}
