package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
sealed interface Message {
    //TODO: the proto seems wrong, use the schema generator to debug why: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/formats.md#protobuf-schema-generator-experimental
    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class Authenticate(
        @ProtoNumber(1) val username: String,
        @ProtoNumber(2) val password: String
    ) : Message

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    object Ping : Message

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class Version(
        @ProtoNumber(1) val versionV1: Int? = null,
        @ProtoNumber(2) val versionV2: Int? = null,
        @ProtoNumber(3) val release: String? = null,
        @ProtoNumber(4) val os: String? = null,
        @ProtoNumber(5) val osVersion: String? = null,
    ) : Message
}