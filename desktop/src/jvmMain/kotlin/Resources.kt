import java.io.File
import java.net.URL

val composeResourcesDir = File(System.getProperty("compose.application.resources.dir"))

fun jarResource(name: String): URL? = Resources.javaClass.getResource(name)

private object Resources
