package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class QueryUsers(
    @ProtoNumber(1) val ids: List<Int> = emptyList(),
    @ProtoNumber(2) val names: List<String> = emptyList()
)