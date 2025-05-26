package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CodecVersion(
    @ProtoNumber(1) val alpha: Int,
    @ProtoNumber(2) val beta: Int,
    @ProtoNumber(3) val preferAlpha: Boolean = true,
    @ProtoNumber(4) val opus: Boolean? = false
)