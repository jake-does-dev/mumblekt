package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
sealed interface Message {
    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class Authenticate(
        @ProtoNumber(1) val username: String,
        @ProtoNumber(2) val password: String
    ) : Message

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    class Ping : Message

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class Version(
        @ProtoNumber(1) val versionV1: Int?,
        @ProtoNumber(2) val versionV2: Int?,
        @ProtoNumber(3) val release: String?,
        @ProtoNumber(4) val os: String?,
        @ProtoNumber(5) val osVersion: String?,
    ) : Message
}