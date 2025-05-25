package dev.jakedoes

import dev.jakedoes.mumble.domain.Version
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val version = Version(1, 2, "hello", "linux", "whatever")
    val bytes = ProtoBuf.encodeToByteArray(version)
    println(bytes.toAsciiHexString())
    val obj = ProtoBuf.decodeFromByteArray<Version>(bytes)
    println(obj)
}

fun ByteArray.toAsciiHexString() = joinToString("") {
    if (it in 32..127) it.toInt().toChar().toString() else
        "{${it.toUByte().toString(16).padStart(2, '0').uppercase()}}"
}