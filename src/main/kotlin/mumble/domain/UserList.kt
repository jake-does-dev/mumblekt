package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class User(
    @ProtoNumber(1) val userId: Long,
    @ProtoNumber(2) val name: String? = null,
    @ProtoNumber(3) val lastSeen: String? = null,
    @ProtoNumber(4) val lastChannel: Long? = null
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserList(
    @ProtoNumber(1) val users: List<User> = emptyList()
)