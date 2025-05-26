package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class SuggestConfig(
    @ProtoNumber(1) val versionV1: Long? = null,
    @ProtoNumber(4) val versionV2: Long? = null,
    @ProtoNumber(2) val positional: Boolean? = null,
    @ProtoNumber(3) val pushToTalk: Boolean? = null
)