package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UDPTunnel(
    @ProtoNumber(1) val packet: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UDPTunnel

        if (!packet.contentEquals(other.packet)) return false

        return true
    }

    override fun hashCode(): Int {
        return packet.contentHashCode()
    }
}