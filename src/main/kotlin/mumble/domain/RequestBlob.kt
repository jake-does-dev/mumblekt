package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class RequestBlob(
    @ProtoNumber(1) val sessionTexture: List<Long> = emptyList(),
    @ProtoNumber(2) val sessionComment: List<Long> = emptyList(),
    @ProtoNumber(3) val channelDescription: List<Long> = emptyList()
)