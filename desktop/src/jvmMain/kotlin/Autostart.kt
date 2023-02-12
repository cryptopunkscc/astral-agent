import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface Autostart : Flow<Boolean>, (Boolean) -> Unit {
    val enabled: Boolean

    object Empty : Autostart, Flow<Boolean> by emptyFlow(), (Boolean) -> Unit by {} {
        override val enabled = false
    }
}
