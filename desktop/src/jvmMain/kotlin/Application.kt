import kotlinx.coroutines.flow.MutableSharedFlow

class Application(
    platform: Platform,
    val autostart: Autostart,
    val astrald: Astrald = Astrald(platform),
) : Platform by platform {
    val events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    val finalizers = mutableListOf<() -> Unit>()
    companion object {
        val Empty = Application(
            platform = Platform.Empty,
            astrald = Astrald(Platform.Empty),
            autostart = Autostart.Empty,
        )
    }
}
