package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ServerConfig(
    @ProtoNumber(1) val maxBandwidth: Long? = null,
    @ProtoNumber(2) val welcomeText: String? = null,
    @ProtoNumber(3) val allowHtml: Boolean? = null,
    @ProtoNumber(4) val messageLength: Long? = null,
    @ProtoNumber(5) val imageMessageLength: Long? = null,
    @ProtoNumber(6) val maxUsers: Long? = null,
    @ProtoNumber(7) val recordingAllowed: Boolean? = null
)