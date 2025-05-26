package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class BanEntry(
    @ProtoNumber(1) val address: ByteArray,
    @ProtoNumber(2) val mask: Int,
    @ProtoNumber(3) val name: String? = null,
    @ProtoNumber(4) val hash: String? = null,
    @ProtoNumber(5) val reason: String? = null,
    @ProtoNumber(6) val start: String? = null,
    @ProtoNumber(7) val duration: Int? = null,
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BanEntry

        if (!address.contentEquals(other.address)) return false
        if (mask != other.mask) return false
        if (name != other.name) return false
        if (hash != other.hash) return false
        if (reason != other.reason) return false
        if (start != other.start) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.contentHashCode()
        result = 31 * result + mask
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (hash?.hashCode() ?: 0)
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (start?.hashCode() ?: 0)
        result = 31 * result + (duration ?: 0)
        return result
    }
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class BanList(
    @ProtoNumber(1) val bans: List<BanEntry> = emptyList(),
    @ProtoNumber(2) val query: Boolean? = false,
)