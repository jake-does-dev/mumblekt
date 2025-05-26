@file:OptIn(ExperimentalSerializationApi::class)

package dev.jakedoes.mumble.protocol

import dev.jakedoes.mumble.domain.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.ByteBuffer
import java.nio.ByteOrder

private val logger = KotlinLogging.logger {  }

private enum class MessageType(val id: ByteArray) {
    Version(byteArrayOf(0x00, 0x00)),
    Authenticate(byteArrayOf(0x00, 0x02)),
    Ping(byteArrayOf(0x00, 0x03)),
    Unrecognised(byteArrayOf(0x00)),
}

private fun findMessageType(id: ByteArray): MessageType =
    when {
        id.contentEquals(byteArrayOf(0x00, 0x00)) -> MessageType.Version
        id.contentEquals(byteArrayOf(0x00, 0x02)) -> MessageType.Authenticate
        id.contentEquals(byteArrayOf(0x00, 0x03)) -> MessageType.Ping
        else -> MessageType.Unrecognised
    }

private fun ByteArray.toAsciiHexString() = joinToString("") {
    if (it in 32..127) it.toInt().toChar().toString() else
        "{${it.toUByte().toString(16).padStart(2, '0').uppercase()}}"
}

object MumbleProtocol {
    fun encode(message: Message): ByteArray =
        when (message) {
            is Authenticate -> encode(MessageType.Authenticate.id, ProtoBuf.encodeToByteArray(message))
            is Ping -> encode(MessageType.Ping.id, ProtoBuf.encodeToByteArray(message))
            is Version -> encode(MessageType.Version.id, ProtoBuf.encodeToByteArray(message))
            is Unrecognised -> NotImplementedError("We cannot explicitly encode messages of type 'Unrecognised'.")
        } as ByteArray

    private fun encode(id: ByteArray, payload: ByteArray): ByteArray {
        val encoding = ByteBuffer.allocate(2 + 4 + payload.size)
            .order(ByteOrder.BIG_ENDIAN)
            .put(id)
            .putInt(payload.size)
            .put(payload)
            .array()

        logger.info { "Sending: ${encoding.toAsciiHexString()}" }
        return encoding
    }

    fun decode(bytes: ByteArray): Message =
        when (findMessageType(bytes.copyOfRange(0, 2))) {
            MessageType.Version -> ProtoBuf.decodeFromByteArray<Version>(bytes.copyOfRange(6, bytes.size))
            MessageType.Authenticate -> ProtoBuf.decodeFromByteArray<Authenticate>(bytes.copyOfRange(6, bytes.size))
            MessageType.Ping -> ProtoBuf.decodeFromByteArray<Ping>(bytes.copyOfRange(6, bytes.size))
            MessageType.Unrecognised -> Unrecognised("Unable to determine incoming message, with the following bytes: $bytes")
        }
}