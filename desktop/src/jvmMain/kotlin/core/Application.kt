package core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.coroutines.CoroutineContext

class Application(
    platform: Platform,
    resources: Resources,
    val autostart: Autostart,
    val astrald: Astrald = Astrald(
        platform = platform,
        resources = resources,
    ),
) : Platform by platform,
    Resources by resources {
    val events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    val finalizers = mutableListOf<() -> Unit>()
    companion object {
        val Empty = Application(
            platform = Platform.Empty,
            autostart = Autostart.Empty,
            resources = Resources.Empty,
        )
    }
    object Root : CoroutineContext.Key<CoroutineContext.Element>
}
