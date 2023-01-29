import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main() {
    val events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    val finalizers = mutableListOf<() -> Unit>()

    println("starting tray")
    val tray = astralTray(events)

    tray.setEnabled(false)
    if (shouldStartAstrald()) {
        val astraldProcess = startAstrald()
        finalizers.add {
            val code = astraldProcess.sigint().waitFor()
            println("astrald sigint $code")
        }
        Thread {
            astraldProcess.waitFor()
            events.tryEmit(Close(astraldProcess))
        }.start()
        tray.status = "Astral connected"
    } else {
        tray.status = "Astral detached"
    }

    tray.setEnabled(true)
    runBlocking {
        events.filterIsInstance<Close>().first()
        println("received close event")
    }
    for (finalize in finalizers) finalize()
    println("finalized")
    exitProcess(0)
}
