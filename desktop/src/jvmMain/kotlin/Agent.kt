import core.Application
import kotlin.coroutines.CoroutineContext

fun agent(
    coroutineContext: CoroutineContext,
    os: String = default.Platform.OS_NAME.lowercase()
): Application = when {
    "nux" in os -> gnome.agent(coroutineContext)
    else -> throw NotImplementedError("Operating system $os, is not supported yet")
}
