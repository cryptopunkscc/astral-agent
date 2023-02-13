package jvm

import core.Application
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.coroutines.CoroutineContext

class Resources(
    coroutineContext: CoroutineContext
) : core.Resources {
    private val source = coroutineContext[Application.Root]!!.javaClass
    override val composeResourcesDir: File get() = File(System.getProperty("compose.application.resources.dir"))
    override fun jarResource(name: String): URL? = source.getResource(name)
    override fun jarResourceStream(name: String): InputStream? = source.getResourceAsStream(name)
}
