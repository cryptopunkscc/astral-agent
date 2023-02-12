package core

import java.io.File
import java.io.InputStream
import java.net.URL

interface Resources {
    val composeResourcesDir: File
    fun jarResource(name: String): URL?
    fun jarResourceStream(name: String): InputStream?

    object Empty : Resources {
        override val composeResourcesDir get() = throw NotImplementedError()
        override fun jarResource(name: String) = throw NotImplementedError()
        override fun jarResourceStream(name: String) = throw NotImplementedError()
    }
}
