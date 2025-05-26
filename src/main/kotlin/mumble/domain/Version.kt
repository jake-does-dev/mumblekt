package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Version(
    @ProtoNumber(1) val versionV1: Int? = null,
    @ProtoNumber(2) val versionV2: Int? = null,
    @ProtoNumber(3) val release: String? = null,
    @ProtoNumber(4) val os: String? = null,
    @ProtoNumber(5) val osVersion: String? = null,
) : Message
