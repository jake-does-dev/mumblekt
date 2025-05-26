package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ChanGroup(
    @ProtoNumber(1) val name: String,
    @ProtoNumber(2) val inherited: Boolean? = true,
    @ProtoNumber(3) val inherit: Boolean? = true,
    @ProtoNumber(4) val inheritable: Boolean? = true,
    @ProtoNumber(5) val add: List<Int> = emptyList(),
    @ProtoNumber(6) val remove: List<Int> = emptyList(),
    @ProtoNumber(7) val inheritedMembers: List<Int> = emptyList(),
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ChanACL(
    @ProtoNumber(1) val applyHere: Boolean? = true,
    @ProtoNumber(2) val applySubs: Boolean? = true,
    @ProtoNumber(3) val inherited: Boolean? = true,
    @ProtoNumber(4) val userId: Int? = null,
    @ProtoNumber(5) val group: String? = null,
    @ProtoNumber(6) val grant: Int? = null,
    @ProtoNumber(7) val deny: Int? = null
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ACL(
    @ProtoNumber(1) val channelId: Int,
    @ProtoNumber(2) val inheritAcls: Boolean? = true,
    @ProtoNumber(3) val groups: List<ChanGroup> = emptyList(),
    @ProtoNumber(4) val acls: List<ChanACL> = emptyList(),
    @ProtoNumber(5) val query: Boolean? = false
)