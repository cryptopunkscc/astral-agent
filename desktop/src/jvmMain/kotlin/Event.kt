sealed interface Event
data class Close(val source: Any): Event
