package compose

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.awaitApplication
import core.Astrald
import core.ProcessInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.system.exitProcess

suspend fun Astrald.shouldStart(): Boolean =
    listDetached().let { list ->
        list.isEmpty() || closeDetachedAstrald(list) { selected ->
            selected.tryClose()
        }.await()
    }

private fun CoroutineScope.closeDetachedAstrald(
    detached: Collection<ProcessInfo>,
    kill: (Collection<ProcessInfo>) -> Collection<ProcessInfo>
): Deferred<Boolean> = async {
    var processes by mutableStateOf(detached)
    var shouldStart = false
    awaitApplication {
        DesktopMaterialTheme {
            Window(
                title = "Astral Agent",
                onCloseRequest = {
                    exitProcess(0)
                },
                icon = painterResource("ic_astral_launcher.svg")
            ) {
                DetachedAstraldScreen(
                    processes = processes,
                    start = ::exitApplication,
                    kill = {
                        processes = kill(it).toList()
                        shouldStart = processes.isEmpty()
                    },
                )
            }
        }
    }
    shouldStart
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun DetachedAstraldScreenPreview() {
    DesktopMaterialTheme {
        DetachedAstraldScreen(
            listOf(
                ProcessInfo(
                    user = "root",
                    pid = 11111L,
                    command = listOf("/bin/astrald")
                )
            )
        )
    }
}

@Composable
private fun DetachedAstraldScreen(
    processes: Collection<ProcessInfo>,
    start: () -> Unit = {},
    kill: (Collection<ProcessInfo>) -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Detached astrald process",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
        )
        val checkboxStates = remember { mutableMapOf<ProcessInfo, MutableState<Boolean>>() }
        processes.forEach {
            if (it !in checkboxStates)
                checkboxStates[it] = mutableStateOf(false)
        }
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
        ) {
            items(processes.toList()) { process ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val checked = checkboxStates.getValue(process)
                    Checkbox(
                        checked = checked.value,
                        onCheckedChange = { checked.value = it }
                    )
                    Text(
                        text = process.pid.toString(),
                    )
                    Text(
                        text = process.user,
                    )
                    Text(
                        text = process.command.joinToString(" "),
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            modifier = Modifier.padding(32.dp),
            text = """
                Astral daemon is already running as separated instance so the GUI cannot take control over the process. 
                Select and kill already running daemon, so GUI will be able to spawn attached one or continue with detached daemon.
                If you want the daemon will closing along with GUI you need to choose attached mode otherwise closing the GUI will not affect the daemon. 
            """.trimIndent()
        )
        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (processes.isNotEmpty()) Button(
                onClick = {
                    val selected = checkboxStates.filterValues { it.value }.keys
                    kill(selected)
                },
                enabled = checkboxStates.values.any { it.value }
            ) {
                Text("kill selected")
            }
            Spacer(Modifier.weight(1f))
            Button(start) {
                Text(
                    if (processes.isEmpty()) "start astrald"
                    else "continue detached"
                )
            }
        }
    }
}
