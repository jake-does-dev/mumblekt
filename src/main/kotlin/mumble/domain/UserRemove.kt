package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserRemove(
    @ProtoNumber(1) val session: Int,
    @ProtoNumber(2) val actor: Int,
    @ProtoNumber(3) val reason: String,
    @ProtoNumber(4) val ban: Boolean,
)
