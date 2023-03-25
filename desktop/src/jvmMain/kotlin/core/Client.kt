package core

import cc.cryptopunks.astral.client.Network
import cc.cryptopunks.astral.client.Stream
import cc.cryptopunks.astral.client.ext.readMessage
import cc.cryptopunks.astral.client.tcp.astralTcpNetwork
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class Client(
    private val agent: Port
) {
    fun setAlias(alias: String): Unit = agent {
        "set_alias"(Alias(alias))
    }

    fun getAlias(): String = agent {
        "get_alias"<Alias>().alias
    }

    private data class Alias(val alias: String)
}

class Port(
    private val cookie: String,
    private val port: String,
    private val net: Network = astralTcpNetwork(),
) {

    operator fun <R> invoke(block: Call.() -> R): R =
        net.query(port).use { stream ->
            Call(stream).run {
                "auth"(Cookie(cookie)) ?: Unit
                block()
            }
        }

    private data class Cookie(val cookie: String)
}

class Call(stream: Stream) : Stream by stream {

    inline operator fun <reified R> String.invoke(): R = invoke(null as String?)

    inline operator fun <reified P, reified R> String.invoke(params: P): R {
        val name = this
        write(mapper.writeValueAsBytes(Method(name, params)) + eol)
        val message = readMessage() ?: throw Exception(name, "Empty response")
        val type = mapper.typeFactory.constructParametricType(Response::class.java, R::class.java)
        val response = mapper.readValue<Response<R>>(message, type)
        if (response.status != "ok") throw Exception(name, response.error)
        return response.data
    }

    data class Response<T>(
        val status: String,
        val data: T,
        val error: String,
    )

    data class Method<P>(
        val method: String,
        val params: P,
    )

    data class Exception(val name: String, val error: String) : Throwable("[$name] $error")

    companion object {
        val mapper = jacksonObjectMapper()
        val eol = "\n".toByteArray()
    }
}
