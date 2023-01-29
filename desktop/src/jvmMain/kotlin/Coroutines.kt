import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
