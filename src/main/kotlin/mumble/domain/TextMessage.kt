package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class TextMessage(
    @ProtoNumber(1) val actor: Int? = null,
    @ProtoNumber(2) val session: List<Int>? = emptyList(),
    @ProtoNumber(6) val channelId: List<Int>? = emptyList(),
    @ProtoNumber(4) val treeId: List<Int>? = emptyList(),
    @ProtoNumber(5) val message: String,
)
