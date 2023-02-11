package compose

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cc.cryptopunks.astral.common.App

fun composeApplication() = application(false) {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
