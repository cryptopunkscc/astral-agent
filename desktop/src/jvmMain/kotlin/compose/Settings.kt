package compose

import Autostart
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun astralAgentSettings() {
    application(false) {
        DesktopMaterialTheme {
            Window(
                title = "Astral Agent",
                onCloseRequest = ::exitApplication,
                icon = painterResource("ic_astral_launcher.svg"),
            ) {
                SettingsScreen()
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    DesktopMaterialTheme {
        SettingsScreen()
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Settings",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
        )
        AutostartOption()
    }
}

@Composable
fun AutostartOption() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val enabled by Autostart.run { collectAsState(enabled) }
        Checkbox(
            checked = enabled,
            onCheckedChange = { enable ->
                Autostart(enable)
            }
        )
        Text(
            text = "Add Astral Agent to startup applications.",
        )
    }
}
