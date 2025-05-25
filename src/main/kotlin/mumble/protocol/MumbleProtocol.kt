@file:OptIn(ExperimentalSerializationApi::class)

package dev.jakedoes.mumble.protocol

import dev.jakedoes.mumble.domain.Authenticate
import dev.jakedoes.mumble.domain.Version
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class MessageInfo(id: ByteArray) {
    Version(byteArrayOf(0x00, 0x00)),
    Authenticate(byteArrayOf(0x00, 0x02)),
    Ping(byteArrayOf(0x00, 0x03)),
}

fun encode(version: Version): ByteArray {
    val id: ByteArray = ByteBuffer.allocate(2)
        .order(ByteOrder.BIG_ENDIAN)
        .putInt(0)
        .array()

    val bytes: ByteArray = ProtoBuf.encodeToByteArray(version)

    val length: ByteArray = ByteBuffer.allocate(4)
        .order(ByteOrder.BIG_ENDIAN)
        .putInt(bytes.size)
        .array()

    return id + length + bytes
}

fun encode(authenticate: Authenticate): ByteArray {
    return TODO("Provide the return value")
}

fun main() {
    encode(Version(release="release"))
}