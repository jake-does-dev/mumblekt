package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Version(
    @ProtoNumber(1) val versionV1: Int? = null,
    @ProtoNumber(2) val release: String? = null,
    @ProtoNumber(3) val os: String? = null,
    @ProtoNumber(4) val osVersion: String? = null,
    @ProtoNumber(5) val versionV2: Long? = null,
) {
    constructor(major: Long,
                minor: Long,
                os: String,
                osVersion: String,
                release: String,
    ) : this(null, release, os, osVersion, (major shl 48) or (minor shl 32))
}