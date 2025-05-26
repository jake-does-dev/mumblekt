package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ContextAction(
    @ProtoNumber(1) val session: Long? = null,
    @ProtoNumber(2) val channelId: Long? = null,
    @ProtoNumber(3) val action: String
)