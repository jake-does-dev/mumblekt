package mumble.protocol

import dev.jakedoes.mumble.domain.Authenticate
import dev.jakedoes.mumble.domain.Ping
import dev.jakedoes.mumble.domain.Version
import dev.jakedoes.mumble.protocol.MumbleProtocol
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class MumbleProtocolTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun version() {
        val version = Version(
            major = 1L,
            minor = 5L,
            os = "Linux",
            osVersion = "Fedora KDE 42",
            release = "1.5.0",
        )
        val serialized = MumbleProtocol.encode(version)
        val deserialized = MumbleProtocol.decode(serialized, Version::class)
        assertEquals(version, deserialized)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun authenticate() {
        val authenticate = Authenticate("jake", "secret-password")
        val serialized = MumbleProtocol.encode(authenticate)
        val deserialized = MumbleProtocol.decode(serialized, Authenticate::class)
        assertEquals(authenticate, deserialized)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun ping() {
        val ping = Ping()
        val serialized = MumbleProtocol.encode(ping)
        val deserialized = MumbleProtocol.decode(serialized, Ping::class)
        assertEquals(ping, deserialized)
    }
}