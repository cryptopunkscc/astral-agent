import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
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
}
