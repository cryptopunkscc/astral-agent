import java.io.File
import java.io.InputStream
import java.net.URL

val composeResourcesDir = File(System.getProperty("compose.application.resources.dir"))

fun jarResource(name: String): URL? = Resources.javaClass.getResource(name)
fun jarResourceStream(name: String): InputStream? = Resources.javaClass.getResourceAsStream(name)

private object Resources
