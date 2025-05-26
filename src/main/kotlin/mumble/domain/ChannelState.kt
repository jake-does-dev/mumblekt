package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ChannelState(
    @ProtoNumber(1) val channelId: Int? = null,
    @ProtoNumber(2) val parent: Int? = null,
    @ProtoNumber(3) val name: String? = null,
    @ProtoNumber(4) val links: List<Int>? = emptyList(),
    @ProtoNumber(5) val description: String? = null,
    @ProtoNumber(6) val linksAdd: List<Int>? = emptyList(),
    @ProtoNumber(7) val linksRemove: List<Int>? = emptyList(),
    @ProtoNumber(8) val temporary: Boolean? = false,
    @ProtoNumber(9) val position: Int? = 0,
    @ProtoNumber(10) val descriptionHash: ByteArray? = null,
    @ProtoNumber(11) val maxUsers: Int? = null,
    @ProtoNumber(12) val isEnterRestricted: Boolean? = null,
    @ProtoNumber(13) val canEnter: Boolean? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelState

        if (channelId != other.channelId) return false
        if (parent != other.parent) return false
        if (temporary != other.temporary) return false
        if (position != other.position) return false
        if (maxUsers != other.maxUsers) return false
        if (isEnterRestricted != other.isEnterRestricted) return false
        if (canEnter != other.canEnter) return false
        if (name != other.name) return false
        if (links != other.links) return false
        if (description != other.description) return false
        if (linksAdd != other.linksAdd) return false
        if (linksRemove != other.linksRemove) return false
        if (!descriptionHash.contentEquals(other.descriptionHash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelId ?: 0
        result = 31 * result + (parent ?: 0)
        result = 31 * result + (temporary?.hashCode() ?: 0)
        result = 31 * result + (position ?: 0)
        result = 31 * result + (maxUsers ?: 0)
        result = 31 * result + (isEnterRestricted?.hashCode() ?: 0)
        result = 31 * result + (canEnter?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (linksAdd?.hashCode() ?: 0)
        result = 31 * result + (linksRemove?.hashCode() ?: 0)
        result = 31 * result + (descriptionHash?.contentHashCode() ?: 0)
        return result
    }
}
