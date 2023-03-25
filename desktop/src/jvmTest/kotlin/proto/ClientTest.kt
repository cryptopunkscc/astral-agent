package proto

import cc.cryptopunks.astral.client.test.TestNetwork
import cc.cryptopunks.astral.client.test.StreamTestCase
import core.Call
import core.Client
import core.Port
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientTest {

    private val net = TestNetwork()
    private val port = Port("test_cookie", "port", net)
    private val client = Client(port)

    @Test
    fun authError() = StreamTestCase(net).run {

        test { client.setAlias("test_alias") }

        assertEquals("""{"method":"auth","params":{"cookie":"test_cookie"}}""", read())

        write("""{"status":"error","data":null,"error":"unauthorized"}""")

        assertEquals(Call.Exception("auth", "unauthorized"), result)
    }

    @Test
    fun setAlias() = StreamTestCase(net).run {

        test { client.setAlias("test_alias") }

        assertEquals("""{"method":"auth","params":{"cookie":"test_cookie"}}""", read())

        write("""{"status":"ok","data":null,"error":""}""")

        assertEquals("""{"method":"set_alias","params":{"alias":"test_alias"}}""", read())

        write("""{"status":"ok","data":null,"error":""}""")

        assertEquals(Unit, result)
    }

    @Test
    fun getAlias() = StreamTestCase(net).run {

        test { client.getAlias() }

        assertEquals("""{"method":"auth","params":{"cookie":"test_cookie"}}""", read())

        write("""{"status":"ok","data":null,"error":""}""")

        assertEquals("""{"method":"get_alias","params":null}""", read())

        write("""{"status":"ok","data":{"alias":"dell"},"error":""}""")

        assertEquals("dell", result)
    }
}
