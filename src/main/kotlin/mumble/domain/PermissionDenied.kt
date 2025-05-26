package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
enum class DenyType {
    Text,
    Permission,
    SuperUser,
    ChannelName,
    TextTooLong,
    H9K,
    TemporaryChannel,
    MissingCertificate,
    UserName,
    ChannelFull,
    NestingLimit,
    ChannelCountLimit,
    ChannelListenerLimit,
    UserListenerLimit
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class PermissionDenied(
    @ProtoNumber(1) val permission: Int? = null,
    @ProtoNumber(2) val channelId: Int? = null,
    @ProtoNumber(3) val session: Int? = null,
    @ProtoNumber(4) val reason: String? = null,
    @ProtoNumber(5) val type: DenyType? = null,
    @ProtoNumber(6) val name: String? = null
)