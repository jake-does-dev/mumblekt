package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Ping(
    @ProtoNumber(1) val timestamp: Long? = null,
    @ProtoNumber(2) val good: Int? = null,
    @ProtoNumber(3) val late: Int? = null,
    @ProtoNumber(4) val lost: Int? = null,
    @ProtoNumber(5) val resync: Int? = null,
    @ProtoNumber(6) val udpPackets: Int? = null,
    @ProtoNumber(7) val tcpPackets: Int? = null,
    @ProtoNumber(8) val udpPingAvg: Float? = null,
    @ProtoNumber(9) val udpPingVar: Float? = null,
    @ProtoNumber(10) val tcpPingAvg: Float? = null,
    @ProtoNumber(11) val tcpPingVar: Float? = null,
)
