package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Stats(
    @ProtoNumber(1) val good: Long? = null,
    @ProtoNumber(2) val late: Long? = null,
    @ProtoNumber(3) val lost: Long? = null,
    @ProtoNumber(4) val resync: Long? = null
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserStatsRollingStats(
    @ProtoNumber(1) val timeWindow: Long? = null,
    @ProtoNumber(2) val fromClient: Stats? = null,
    @ProtoNumber(3) val fromServer: Stats? = null
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserStats(
    @ProtoNumber(1) val session: Long? = null,
    @ProtoNumber(2) val statsOnly: Boolean? = false,
    @ProtoNumber(3) val certificates: List<ByteArray> = emptyList(),
    @ProtoNumber(4) val fromClient: Stats? = null,
    @ProtoNumber(5) val fromServer: Stats? = null,
    @ProtoNumber(6) val udpPackets: Long? = null,
    @ProtoNumber(7) val tcpPackets: Long? = null,
    @ProtoNumber(8) val udpPingAvg: Float? = null,
    @ProtoNumber(9) val udpPingVar: Float? = null,
    @ProtoNumber(10) val tcpPingAvg: Float? = null,
    @ProtoNumber(11) val tcpPingVar: Float? = null,
    @ProtoNumber(12) val version: CodecVersion? = null,
    @ProtoNumber(13) val celtVersions: List<Int> = emptyList(),
    @ProtoNumber(14) val address: ByteArray? = null,
    @ProtoNumber(15) val bandwidth: Long? = null,
    @ProtoNumber(16) val onlinesecs: Long? = null,
    @ProtoNumber(17) val idlesecs: Long? = null,
    @ProtoNumber(18) val strongCertificate: Boolean? = false,
    @ProtoNumber(19) val opus: Boolean? = false,
    @ProtoNumber(20) val rollingStats: UserStatsRollingStats? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserStats

        if (session != other.session) return false
        if (statsOnly != other.statsOnly) return false

        if (certificates.size != other.certificates.size || !certificates.zip(other.certificates)
                .all { (a, b) -> a.contentEquals(b) }
        ) return false
        if (fromClient != other.fromClient) return false
        if (fromServer != other.fromServer) return false
        if (udpPackets != other.udpPackets) return false
        if (tcpPackets != other.tcpPackets) return false
        if (udpPingAvg != other.udpPingAvg) return false
        if (udpPingVar != other.udpPingVar) return false
        if (tcpPingAvg != other.tcpPingAvg) return false
        if (tcpPingVar != other.tcpPingVar) return false
        if (version != other.version) return false
        if (celtVersions != other.celtVersions) return false
        if (!address.contentEquals(other.address)) return false
        if (bandwidth != other.bandwidth) return false
        if (onlinesecs != other.onlinesecs) return false
        if (idlesecs != other.idlesecs) return false
        if (strongCertificate != other.strongCertificate) return false
        if (opus != other.opus) return false
        if (rollingStats != other.rollingStats) return false

        return true
    }

    override fun hashCode(): Int {
        var result = session?.hashCode() ?: 0
        result = 31 * result + (statsOnly?.hashCode() ?: 0)
        result = 31 * result + certificates.sumOf { it.contentHashCode() }
        result = 31 * result + (fromClient?.hashCode() ?: 0)
        result = 31 * result + (fromServer?.hashCode() ?: 0)
        result = 31 * result + (udpPackets?.hashCode() ?: 0)
        result = 31 * result + (tcpPackets?.hashCode() ?: 0)
        result = 31 * result + (udpPingAvg?.hashCode() ?: 0)
        result = 31 * result + (udpPingVar?.hashCode() ?: 0)
        result = 31 * result + (tcpPingAvg?.hashCode() ?: 0)
        result = 31 * result + (tcpPingVar?.hashCode() ?: 0)
        result = 31 * result + (version?.hashCode() ?: 0)
        result = 31 * result + celtVersions.hashCode()
        result = 31 * result + (address?.contentHashCode() ?: 0)
        result = 31 * result + (bandwidth?.hashCode() ?: 0)
        result = 31 * result + (onlinesecs?.hashCode() ?: 0)
        result = 31 * result + (idlesecs?.hashCode() ?: 0)
        result = 31 * result + (strongCertificate?.hashCode() ?: 0)
        result = 31 * result + (opus?.hashCode() ?: 0)
        result = 31 * result + (rollingStats?.hashCode() ?: 0)
        return result
    }
}