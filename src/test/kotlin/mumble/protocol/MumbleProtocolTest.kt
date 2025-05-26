package mumble.protocol

import dev.jakedoes.mumble.domain.Authenticate
import dev.jakedoes.mumble.domain.Message
import dev.jakedoes.mumble.domain.Message.*
import dev.jakedoes.mumble.domain.Ping
import dev.jakedoes.mumble.domain.Version
import dev.jakedoes.mumble.protocol.MumbleProtocol
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

class MumbleProtocolTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun version() {
        val version: Message = Version(1, 2, "hello", "linux", "whatever")
        val serialized = MumbleProtocol.encode(version)
        val deserialized = MumbleProtocol.decode(serialized)
        assertEquals(version, deserialized)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun authenticate() {
        val authenticate: Message = Authenticate("jake", "secret-password")
        val serialized = MumbleProtocol.encode(authenticate)
        val deserialized = MumbleProtocol.decode(serialized)
        assertEquals(authenticate, deserialized)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun ping() {
        val ping: Message = Ping
        val serialized = MumbleProtocol.encode(ping)
        val deserialized = MumbleProtocol.decode(serialized)
        assertEquals(ping, deserialized)
    }
}