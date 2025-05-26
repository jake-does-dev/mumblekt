package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ServerSync(
    @ProtoNumber(1) val session: Int? = null,
    @ProtoNumber(2) val maxBandwidth: Int? = null,
    @ProtoNumber(3) val welcomeText: String? = null,
    @ProtoNumber(4) val permissions: Long? = null,
)
