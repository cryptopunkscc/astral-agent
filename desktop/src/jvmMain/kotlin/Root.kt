import kotlin.coroutines.CoroutineContext

object Root : CoroutineContext.Element {
    override val key = core.Application.Root
}
