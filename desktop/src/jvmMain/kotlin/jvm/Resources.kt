package jvm

import java.io.File
import java.io.InputStream
import java.net.URL

class Resources(
    root: Any
) : core.Resources {
    private val source = root.javaClass
    override val composeResourcesDir: File get() = File(System.getProperty("compose.application.resources.dir"))
    override fun jarResource(name: String): URL? = source.getResource(name)
    override fun jarResourceStream(name: String): InputStream? = source.getResourceAsStream(name)
}
