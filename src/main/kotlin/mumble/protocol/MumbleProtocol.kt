@file:OptIn(ExperimentalSerializationApi::class)

package dev.jakedoes.mumble.protocol

import dev.jakedoes.mumble.domain.Authenticate
import dev.jakedoes.mumble.domain.ChannelState
import dev.jakedoes.mumble.domain.CryptSetup
import dev.jakedoes.mumble.domain.Ping
import dev.jakedoes.mumble.domain.TextMessage
import dev.jakedoes.mumble.domain.Version
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger { }

private fun ByteArray.toAsciiHexString() = joinToString("") {
    if (it in 32..127) it.toInt().toChar().toString() else
        "{${it.toUByte().toString(16).padStart(2, '0').uppercase()}}"
}

object MumbleProtocol {
    fun <T : Any> encode(message: T): ByteArray =
        when (message) {
            is Version -> {
                logger.info { "Encoded a [Version], yielding the following hex: ${ProtoBuf.encodeToHexString<Version>(message)}" }
                encode(MessageType.Version.id, ProtoBuf.encodeToByteArray<Version>(message))
            }
            is Authenticate -> {
                logger.info { "Encoded a [Authenticate], yielding the following hex: ${ProtoBuf.encodeToHexString<Authenticate>(message)}" }
                encode(MessageType.Authenticate.id, ProtoBuf.encodeToByteArray<Authenticate>(message))
            }
            is Ping -> {
                logger.info { "Encoded a [Ping], yielding the following hex: ${ProtoBuf.encodeToHexString<Ping>(message)}" }
                encode(MessageType.Ping.id, ProtoBuf.encodeToByteArray<Ping>(message))
            }
            is ChannelState -> {
                logger.info { "Encoded a [ChannelState], yielding the following hex: ${ProtoBuf.encodeToHexString<ChannelState>(message)}" }
                encode(MessageType.ChannelState.id, ProtoBuf.encodeToByteArray<ChannelState>(message))
            }
            is CryptSetup -> {
                logger.info { "Encoded a [CryptSetup], yielding the following hex: ${ProtoBuf.encodeToHexString<CryptSetup>(message)}" }
                encode(MessageType.CryptSetup.id, ProtoBuf.encodeToByteArray<CryptSetup>(message))
            }
            is TextMessage -> {
                logger.info { "Encoded a [TextMessage], yielding the following hex: ${ProtoBuf.encodeToHexString<TextMessage>(message)}" }
                encode(MessageType.TextMessage.id, ProtoBuf.encodeToByteArray<TextMessage>(message))
            }
            else -> throw IllegalArgumentException("Unknown type for encoding: ${message.javaClass}")
        }

    private fun encode(id: Short, payload: ByteArray): ByteArray {
        val encoding = ByteBuffer.allocate(2 + 4 + payload.size)
            .order(ByteOrder.BIG_ENDIAN)
            .putShort(id)
            .putInt(payload.size)
            .put(payload)
            .array()

        logger.info { "Sending: ${encoding.toAsciiHexString()}" }
        return encoding
    }

    inline fun <reified T : Any> decode(bytes: ByteArray, clazz: KClass<T>): T =
        decodePayload(bytes.copyOfRange(6, bytes.size), clazz)

    inline fun <reified T : Any> decodePayload(payload: ByteArray, clazz: KClass<T>): T {
        return when (clazz) {
            Version::class -> ProtoBuf.decodeFromByteArray<Version>(payload) as T
            Authenticate::class -> ProtoBuf.decodeFromByteArray<Authenticate>(payload) as T
            Ping::class -> ProtoBuf.decodeFromByteArray<Ping>(payload) as T
            ChannelState::class -> ProtoBuf.decodeFromByteArray<ChannelState>(payload) as T
            CryptSetup::class -> ProtoBuf.decodeFromByteArray<CryptSetup>(payload) as T
            TextMessage::class -> ProtoBuf.decodeFromByteArray<TextMessage>(payload) as T
            else -> throw IllegalArgumentException("Unknown type for decoding: ${clazz.simpleName}")
        }
    }
}