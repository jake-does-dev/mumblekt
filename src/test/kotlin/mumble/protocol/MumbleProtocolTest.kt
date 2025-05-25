package mumble.protocol

import dev.jakedoes.mumble.domain.Message
import dev.jakedoes.mumble.domain.Message.Authenticate
import dev.jakedoes.mumble.domain.Message.Ping
import dev.jakedoes.mumble.domain.Message.Version
import dev.jakedoes.mumble.protocol.decode
import dev.jakedoes.mumble.protocol.encode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

class MumbleProtocolTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun version() {
        val version: Message = Version(1, 2, "hello", "linux", "whatever")
        val serialized = encode(version)
        val deserialized = decode(serialized)
        assertEquals(version, deserialized)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun authenticate() {
        val authenticate: Message = Authenticate("jake", "secret-password")
        val serialized = encode(authenticate)
        val deserialized = decode(serialized)
        assertEquals(authenticate, deserialized)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun ping() {
        val ping: Message = Ping
        val serialized = encode(ping)
        val deserialized = decode(serialized)
        assertEquals(ping, deserialized)
    }
}