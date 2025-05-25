@file:OptIn(ExperimentalSerializationApi::class)

package dev.jakedoes.mumble.protocol

import dev.jakedoes.mumble.domain.Message
import dev.jakedoes.mumble.domain.Message.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun encode(message: Message): ByteArray {
    val bytes: ByteArray = ProtoBuf.encodeToByteArray(message)

    val length: ByteArray = ByteBuffer.allocate(4)
        .order(ByteOrder.BIG_ENDIAN)
        .putInt(bytes.size)
        .array()

    return message.id() + length + bytes
}

fun decode(bytes: ByteArray): Message {
    return ProtoBuf.decodeFromByteArray<Message>(bytes.copyOfRange(6, bytes.size))
}

private fun Message.id(): ByteArray {
    return when (this) {
        is Version -> byteArrayOf(0x00, 0x00)
        is Authenticate -> byteArrayOf(0x00, 0x02)
        is Ping -> byteArrayOf(0x00, 0x03)
    }
}