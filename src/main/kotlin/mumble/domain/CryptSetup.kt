package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CryptSetup(
    @ProtoNumber(1) val key: ByteArray? = null,
    @ProtoNumber(2) val clientNonce: ByteArray? = null,
    @ProtoNumber(3) val serverNonce: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CryptSetup

        if (!key.contentEquals(other.key)) return false
        if (!clientNonce.contentEquals(other.clientNonce)) return false
        if (!serverNonce.contentEquals(other.serverNonce)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key?.contentHashCode() ?: 0
        result = 31 * result + (clientNonce?.contentHashCode() ?: 0)
        result = 31 * result + (serverNonce?.contentHashCode() ?: 0)
        return result
    }
}