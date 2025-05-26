package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class VolumeAdjustment(
    @ProtoNumber(1) val listeningChannel: Int? = null,
    @ProtoNumber(2) val volumeAdjustment: Float? = null,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserState(
    @ProtoNumber(1) val session: Int? = null,
    @ProtoNumber(2) val actor: Int? = null,
    @ProtoNumber(3) val name: String? = null,
    @ProtoNumber(4) val userId: Int? = null,
    @ProtoNumber(5) val channelId: Int? = null,
    @ProtoNumber(6) val mute: Boolean? = null,
    @ProtoNumber(7) val deaf: Boolean? = null,
    @ProtoNumber(8) val suppress: Boolean? = null,
    @ProtoNumber(9) val selfMute: Boolean? = null,
    @ProtoNumber(10) val selfDeaf: Boolean? = null,
    @ProtoNumber(11) val texture: ByteArray? = null,
    @ProtoNumber(12) val pluginContext: ByteArray? = null,
    @ProtoNumber(13) val pluginIdentity: String? = null,
    @ProtoNumber(14) val comment: String? = null,
    @ProtoNumber(15) val hash: String? = null,
    @ProtoNumber(16) val commentHash: ByteArray? = null,
    @ProtoNumber(17) val textureHash: ByteArray? = null,
    @ProtoNumber(18) val prioritySpeaker: Boolean? = null,
    @ProtoNumber(19) val recording: Boolean? = null,
    @ProtoNumber(20) val temporaryAccessTokens: List<String> = emptyList(),
    @ProtoNumber(21) val listeningChannelAdd: List<Int> = emptyList(),
    @ProtoNumber(22) val listeningChannelRemove: List<Int> = emptyList(),
    @ProtoNumber(23) val listeningVolumeAdjustment: List<VolumeAdjustment> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserState

        if (session != other.session) return false
        if (actor != other.actor) return false
        if (userId != other.userId) return false
        if (channelId != other.channelId) return false
        if (mute != other.mute) return false
        if (deaf != other.deaf) return false
        if (suppress != other.suppress) return false
        if (selfMute != other.selfMute) return false
        if (selfDeaf != other.selfDeaf) return false
        if (prioritySpeaker != other.prioritySpeaker) return false
        if (recording != other.recording) return false
        if (name != other.name) return false
        if (!texture.contentEquals(other.texture)) return false
        if (!pluginContext.contentEquals(other.pluginContext)) return false
        if (pluginIdentity != other.pluginIdentity) return false
        if (comment != other.comment) return false
        if (hash != other.hash) return false
        if (!commentHash.contentEquals(other.commentHash)) return false
        if (!textureHash.contentEquals(other.textureHash)) return false
        if (temporaryAccessTokens != other.temporaryAccessTokens) return false
        if (listeningChannelAdd != other.listeningChannelAdd) return false
        if (listeningChannelRemove != other.listeningChannelRemove) return false
        if (listeningVolumeAdjustment != other.listeningVolumeAdjustment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = session ?: 0
        result = 31 * result + (actor ?: 0)
        result = 31 * result + (userId ?: 0)
        result = 31 * result + (channelId ?: 0)
        result = 31 * result + (mute?.hashCode() ?: 0)
        result = 31 * result + (deaf?.hashCode() ?: 0)
        result = 31 * result + (suppress?.hashCode() ?: 0)
        result = 31 * result + (selfMute?.hashCode() ?: 0)
        result = 31 * result + (selfDeaf?.hashCode() ?: 0)
        result = 31 * result + (prioritySpeaker?.hashCode() ?: 0)
        result = 31 * result + (recording?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (texture?.contentHashCode() ?: 0)
        result = 31 * result + (pluginContext?.contentHashCode() ?: 0)
        result = 31 * result + (pluginIdentity?.hashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (hash?.hashCode() ?: 0)
        result = 31 * result + (commentHash?.contentHashCode() ?: 0)
        result = 31 * result + (textureHash?.contentHashCode() ?: 0)
        result = 31 * result + temporaryAccessTokens.hashCode()
        result = 31 * result + listeningChannelAdd.hashCode()
        result = 31 * result + listeningChannelRemove.hashCode()
        result = 31 * result + listeningVolumeAdjustment.hashCode()
        return result
    }
}
