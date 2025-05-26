package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class PermissionQuery(
    @ProtoNumber(1) val channelId: Long? = null,
    @ProtoNumber(2) val permissions: Long? = null,
    @ProtoNumber(3) val flush: Boolean? = false
)