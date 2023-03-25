package compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.launchApplication
import core.Application
import core.Autostart
import core.Client
import kotlinx.coroutines.launch

fun Application.astralAgentSettings() = launchApplication {
    DesktopMaterialTheme {
        Window(
            title = "Astral Agent",
            onCloseRequest = ::exitApplication,
            icon = painterResource("ic_astral_launcher.svg"),
            state = WindowState(
                size = DpSize(
                    height = Dp.Unspecified,
                    width = 500.dp
                ),
                position = WindowPosition.Aligned(Alignment.TopEnd)
            )
        ) {
            SettingsScreen()
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    DesktopMaterialTheme {
        Application.Empty.SettingsScreen()
    }
}

@Composable
private fun Application.SettingsScreen() {
    Column(
        modifier = Modifier.padding(32.dp).padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Settings",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        AliasOption(client)
        AutostartOption(autostart)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AliasOption(
    client: Client,
) {
    val scope = rememberCoroutineScope()

    var current by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        progress = true
        val alias = client.getAlias()
        current = alias
        text = alias
        progress = false
    }
    Row(
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = { text = it },
            label = { Text("Alias") },
        )
        AnimatedContent(
            targetState = when {
                text.length < 3 -> 0
                current != text -> 1
                progress -> 2
                else -> -1
            },
            transitionSpec = {
                fadeIn() with fadeOut()
            }
        ) {
            when (it) {
                -1 -> Unit

                2 -> CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = LocalContentColor.current,
                )

                0 -> {
                    IconButton(
                        onClick = { text = current }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }

                else -> Row {
                    IconButton(
                        onClick = {
                            scope.launch {
                                progress = true
                                client.runCatching {
                                    setAlias(text)
                                }.onSuccess {
                                    current = text
                                }.onFailure {
                                    text = current
                                }
                                progress = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = { text = current }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AutostartOption(
    autostart: Autostart,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Add Astral Agent to startup applications.",
            modifier = Modifier.weight(1f),
        )
        val enabled by autostart.run { collectAsState(enabled) }
        Checkbox(
            checked = enabled,
            onCheckedChange = { enable ->
                autostart(enable)
            }
        )
    }
}
