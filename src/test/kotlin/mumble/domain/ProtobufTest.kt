package mumble.domain

import dev.jakedoes.mumble.domain.Message
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
        val version: Message = Message.Version(1, 2, "hello", "linux", "whatever")
        val serialized = ProtoBuf.encodeToByteArray(version)
        val deserialized = ProtoBuf.decodeFromByteArray<Message>(serialized)
        assertEquals(version, deserialized)
    }
}