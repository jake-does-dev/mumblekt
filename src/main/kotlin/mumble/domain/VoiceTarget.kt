package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Target(
    @ProtoNumber(1) val session: List<Long> = emptyList(),
    @ProtoNumber(2) val channelId: Long? = null,
    @ProtoNumber(3) val group: String? = null,
    @ProtoNumber(4) val links: Boolean? = false,
    @ProtoNumber(5) val children: Boolean? = false
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class VoiceTarget(
    @ProtoNumber(1) val id: Long? = null,
    @ProtoNumber(2) val targets: List<Target> = emptyList()
)