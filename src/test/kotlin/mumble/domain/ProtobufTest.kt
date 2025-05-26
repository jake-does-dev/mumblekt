package mumble.domain

import dev.jakedoes.mumble.domain.Version
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals

class ProtobufTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun encodeDecodeTest() {
        val version = Version(
            major = 1L,
            minor = 5L,
            os = "Linux",
            osVersion = "Fedora KDE 42",
            release = "1.5.0",
        )
        val serialized = ProtoBuf.encodeToByteArray(version)
        val deserialized = ProtoBuf.decodeFromByteArray<Version>(serialized)
        assertEquals(version, deserialized)
    }
}