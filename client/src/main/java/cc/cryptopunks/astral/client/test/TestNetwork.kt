package cc.cryptopunks.astral.client.test

import cc.cryptopunks.astral.client.Network
import cc.cryptopunks.astral.client.Port
import cc.cryptopunks.astral.client.Stream
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.concurrent.CompletableFuture

class TestNetwork(
    var identity: String = "test_identity"
) : Network {

    lateinit var stream: Stream

    override fun query(port: String, identity: String): Stream = stream

    override fun register(port: String): Port {
        TODO("Not yet implemented")
    }

    override fun identity(): String = identity
}


class StreamTestCase(
    net: TestNetwork
) {
    private val stream = net.testStream()
    private val completable = CompletableFuture<Any>()
    private val reader = stream.input.bufferedReader()
    private val writer = stream.output.writer()
    fun read(): String = reader.readLine()

    fun write(string: String) {
        writer.write(string)
        writer.flush()
    }
    var result: Any
        get() = completable.join()
        set(value) {
            completable.complete(value)
        }

    fun test(block: () -> Any) {
        Thread {
            result = try {
                block()
            } catch (e: Throwable) {
                e
            }
        }.start()
    }

    private fun TestNetwork.testStream() = run {
        val client = pipedStream()
        val service = pipedStream()
        stream = client
        client.output.connect(service.input)
        service.output.connect(client.input)
        service
    }
    private fun pipedStream() = TestStream(
        input = PipedInputStream(),
        output = PipedOutputStream(),
    )
    private class TestStream<I : InputStream, O : OutputStream>(
        override val input: I,
        override val output: O,
    ) : Stream
}
