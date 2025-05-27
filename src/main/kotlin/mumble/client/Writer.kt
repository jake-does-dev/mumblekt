package dev.jakedoes.mumble.client

import dev.jakedoes.mumble.domain.ACL
import dev.jakedoes.mumble.domain.Authenticate
import dev.jakedoes.mumble.domain.BanList
import dev.jakedoes.mumble.domain.ChannelRemove
import dev.jakedoes.mumble.domain.ChannelState
import dev.jakedoes.mumble.domain.CodecVersion
import dev.jakedoes.mumble.domain.ContextAction
import dev.jakedoes.mumble.domain.ContextActionModify
import dev.jakedoes.mumble.domain.CryptSetup
import dev.jakedoes.mumble.domain.PermissionDenied
import dev.jakedoes.mumble.domain.PermissionQuery
import dev.jakedoes.mumble.domain.Ping
import dev.jakedoes.mumble.domain.QueryUsers
import dev.jakedoes.mumble.domain.Reject
import dev.jakedoes.mumble.domain.RequestBlob
import dev.jakedoes.mumble.domain.ServerConfig
import dev.jakedoes.mumble.domain.ServerSync
import dev.jakedoes.mumble.domain.SuggestConfig
import dev.jakedoes.mumble.domain.TextMessage
import dev.jakedoes.mumble.domain.UDPTunnel
import dev.jakedoes.mumble.domain.UserList
import dev.jakedoes.mumble.domain.UserRemove
import dev.jakedoes.mumble.domain.UserState
import dev.jakedoes.mumble.domain.UserStats
import dev.jakedoes.mumble.domain.Version
import dev.jakedoes.mumble.domain.VoiceTarget
import dev.jakedoes.mumble.protocol.MumbleProtocol
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeByteArray

internal class Writer(private val writer: ByteWriteChannel) {
    /** Duplicating to make it clear to the user what message types are acceptable, instead of just `T`.
     * Again, limitations to `kotlinx.serialization.protobuf` mean that we cannot use a `sealed interface Message`
     * @see [[MumbleProtocol]]
     */
    suspend fun write(message: ACL) {
        writeInternal(message)
    }

    suspend fun write(message: Authenticate) {
        writeInternal(message)
    }

    suspend fun write(message: BanList) {
        writeInternal(message)
    }

    suspend fun write(message: ChannelRemove) {
        writeInternal(message)
    }

    suspend fun write(message: ChannelState) {
        writeInternal(message)
    }

    suspend fun write(message: CodecVersion) {
        writeInternal(message)
    }

    suspend fun write(message: ContextAction) {
        writeInternal(message)
    }

    suspend fun write(message: ContextActionModify) {
        writeInternal(message)
    }

    suspend fun write(message: CryptSetup) {
        writeInternal(message)
    }

    suspend fun write(message: PermissionDenied) {
        writeInternal(message)
    }

    suspend fun write(message: PermissionQuery) {
        writeInternal(message)
    }

    suspend fun write(message: Ping) {
        writeInternal(message)
    }

    suspend fun write(message: QueryUsers) {
        writeInternal(message)
    }

    suspend fun write(message: Reject) {
        writeInternal(message)
    }

    suspend fun write(message: RequestBlob) {
        writeInternal(message)
    }

    suspend fun write(message: ServerConfig) {
        writeInternal(message)
    }

    suspend fun write(message: ServerSync) {
        writeInternal(message)
    }

    suspend fun write(message: SuggestConfig) {
        writeInternal(message)
    }

    suspend fun write(message: TextMessage) {
        writeInternal(message)
    }

    suspend fun write(message: UDPTunnel) {
        writeInternal(message)
    }

    suspend fun write(message: UserList) {
        writeInternal(message)
    }

    suspend fun write(message: UserRemove) {
        writeInternal(message)
    }

    suspend fun write(message: UserState) {
        writeInternal(message)
    }

    suspend fun write(message: UserStats) {
        writeInternal(message)
    }

    suspend fun write(message: Version) {
        writeInternal(message)
    }

    suspend fun write(message: VoiceTarget) {
        writeInternal(message)
    }

    private suspend fun <T : Any> writeInternal(message: T) {
        val packet = MumbleProtocol.encode(message)
        writer.writeByteArray(packet)
        writer.flush()
    }
}