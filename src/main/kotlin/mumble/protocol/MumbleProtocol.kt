@file:OptIn(ExperimentalSerializationApi::class)

package dev.jakedoes.mumble.protocol

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
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger { }

private fun ByteArray.toAsciiHexString() = joinToString("") {
    if (it in 32..127) it.toInt().toChar().toString() else
        "{${it.toUByte().toString(16).padStart(2, '0').uppercase()}}"
}

/**
 * The ideal way to do this would be to have a `sealed interface`, and each of the serializable data classes implement
 * that interface. Gives us exhaustive branches, and safety, which is nice.
 *
 * However, if we do this, then the serialized messages out of the Protobuf encoder have the fully qualified class
 * name as part of its message - i.e., `dev.jakedoes.mumble.protocol.TextMessage`. Whilst this is valid for the
 * Protobuf implementation, it does not play ball with Mumble's protocol. Consequently, we have to take a bit of
 * verbosity here.
 * @see [[dev.jakedoes.mumble.MumbleClient.write]]
 */
object MumbleProtocol {
    fun <T : Any> encode(message: T): ByteArray =
        when (message) {
            is ACL -> {
                logger.debug { "Encoded a [ACL], yielding the following hex: ${ProtoBuf.encodeToHexString<ACL>(message)}" }
                encode(MessageType.ACL.id, ProtoBuf.encodeToByteArray<ACL>(message))
            }
            is Authenticate -> {
                logger.debug { "Encoded a [Authenticate], yielding the following hex: ${ProtoBuf.encodeToHexString<Authenticate>(message)}" }
                encode(MessageType.Authenticate.id, ProtoBuf.encodeToByteArray<Authenticate>(message))
            }
            is BanList -> {
                logger.debug { "Encoded a [BanList], yielding the following hex: ${ProtoBuf.encodeToHexString<BanList>(message)}" }
                encode(MessageType.BanList.id, ProtoBuf.encodeToByteArray<BanList>(message))
            }
            is ChannelRemove -> {
                logger.debug { "Encoded a [ChannelRemove], yielding the following hex: ${ProtoBuf.encodeToHexString<ChannelRemove>(message)}" }
                encode(MessageType.ChannelRemove.id, ProtoBuf.encodeToByteArray<ChannelRemove>(message))
            }
            is ChannelState -> {
                logger.debug { "Encoded a [ChannelState], yielding the following hex: ${ProtoBuf.encodeToHexString<ChannelState>(message)}" }
                encode(MessageType.ChannelState.id, ProtoBuf.encodeToByteArray<ChannelState>(message))
            }
            is CodecVersion -> {
                logger.debug { "Encoded a [CodecVersion], yielding the following hex: ${ProtoBuf.encodeToHexString<CodecVersion>(message)}" }
                encode(MessageType.CodecVersion.id, ProtoBuf.encodeToByteArray<CodecVersion>(message))
            }
            is ContextAction -> {
                logger.debug { "Encoded a [ContextAction], yielding the following hex: ${ProtoBuf.encodeToHexString<ContextAction>(message)}" }
                encode(MessageType.ContextAction.id, ProtoBuf.encodeToByteArray<ContextAction>(message))
            }
            is ContextActionModify -> {
                logger.debug { "Encoded a [ContextActionModify], yielding the following hex: ${ProtoBuf.encodeToHexString<ContextActionModify>(message)}" }
                encode(MessageType.ContextActionModify.id, ProtoBuf.encodeToByteArray<ContextActionModify>(message))
            }
            is CryptSetup -> {
                logger.debug { "Encoded a [CryptSetup], yielding the following hex: ${ProtoBuf.encodeToHexString<CryptSetup>(message)}" }
                encode(MessageType.CryptSetup.id, ProtoBuf.encodeToByteArray<CryptSetup>(message))
            }
            is PermissionDenied -> {
                logger.debug { "Encoded a [PermissionDenied], yielding the following hex: ${ProtoBuf.encodeToHexString<PermissionDenied>(message)}" }
                encode(MessageType.PermissionDenied.id, ProtoBuf.encodeToByteArray<PermissionDenied>(message))
            }
            is PermissionQuery -> {
                logger.debug { "Encoded a [PermissionQuery], yielding the following hex: ${ProtoBuf.encodeToHexString<PermissionQuery>(message)}" }
                encode(MessageType.PermissionQuery.id, ProtoBuf.encodeToByteArray<PermissionQuery>(message))
            }
            is Ping -> {
                logger.debug { "Encoded a [Ping], yielding the following hex: ${ProtoBuf.encodeToHexString<Ping>(message)}" }
                encode(MessageType.Ping.id, ProtoBuf.encodeToByteArray<Ping>(message))
            }
            is QueryUsers -> {
                logger.debug { "Encoded a [QueryUsers], yielding the following hex: ${ProtoBuf.encodeToHexString<QueryUsers>(message)}" }
                encode(MessageType.QueryUsers.id, ProtoBuf.encodeToByteArray<QueryUsers>(message))
            }
            is Reject -> {
                logger.debug { "Encoded a [Reject], yielding the following hex: ${ProtoBuf.encodeToHexString<Reject>(message)}" }
                encode(MessageType.Reject.id, ProtoBuf.encodeToByteArray<Reject>(message))
            }
            is RequestBlob -> {
                logger.debug { "Encoded a [RequestBlob], yielding the following hex: ${ProtoBuf.encodeToHexString<RequestBlob>(message)}" }
                encode(MessageType.RequestBlob.id, ProtoBuf.encodeToByteArray<RequestBlob>(message))
            }
            is ServerSync -> {
                logger.debug { "Encoded a [ServerSync], yielding the following hex: ${ProtoBuf.encodeToHexString<ServerSync>(message)}" }
                encode(MessageType.ServerSync.id, ProtoBuf.encodeToByteArray<ServerSync>(message))
            }
            is ServerConfig -> {
                logger.debug { "Encoded a [ServerConfig], yielding the following hex: ${ProtoBuf.encodeToHexString<ServerConfig>(message)}" }
                encode(MessageType.ServerConfig.id, ProtoBuf.encodeToByteArray<ServerConfig>(message))
            }
            is TextMessage -> {
                logger.debug { "Encoded a [TextMessage], yielding the following hex: ${ProtoBuf.encodeToHexString<TextMessage>(message)}" }
                encode(MessageType.TextMessage.id, ProtoBuf.encodeToByteArray<TextMessage>(message))
            }
            is UDPTunnel -> {
                logger.debug { "Encoded a [UDPTunnel], yielding the following hex: ${ProtoBuf.encodeToHexString<UDPTunnel>(message)}" }
                encode(MessageType.UDPTunnel.id, ProtoBuf.encodeToByteArray<UDPTunnel>(message))
            }
            is UserList -> {
                logger.debug { "Encoded a [UserList], yielding the following hex: ${ProtoBuf.encodeToHexString<UserList>(message)}" }
                encode(MessageType.UserList.id, ProtoBuf.encodeToByteArray<UserList>(message))
            }
            is UserRemove -> {
                logger.debug { "Encoded a [UserRemove], yielding the following hex: ${ProtoBuf.encodeToHexString<UserRemove>(message)}" }
                encode(MessageType.UserRemove.id, ProtoBuf.encodeToByteArray<UserRemove>(message))
            }
            is UserState -> {
                logger.debug { "Encoded a [UserState], yielding the following hex: ${ProtoBuf.encodeToHexString<UserState>(message)}" }
                encode(MessageType.UserState.id, ProtoBuf.encodeToByteArray<UserState>(message))
            }
            is UserStats -> {
                logger.debug { "Encoded a [UserStats], yielding the following hex: ${ProtoBuf.encodeToHexString<UserStats>(message)}" }
                encode(MessageType.UserStats.id, ProtoBuf.encodeToByteArray<UserStats>(message))
            }
            is Version -> {
                logger.debug { "Encoded a [Version], yielding the following hex: ${ProtoBuf.encodeToHexString<Version>(message)}" }
                encode(MessageType.Version.id, ProtoBuf.encodeToByteArray<Version>(message))
            }
            is VoiceTarget -> {
                logger.debug { "Encoded a [VoiceTarget], yielding the following hex: ${ProtoBuf.encodeToHexString<VoiceTarget>(message)}" }
                encode(MessageType.VoiceTarget.id, ProtoBuf.encodeToByteArray<VoiceTarget>(message))
            }
            else -> throw IllegalArgumentException("Unknown type for encoding: ${message.javaClass}")
        }

    private fun encode(id: Short, payload: ByteArray): ByteArray {
        val encoding = ByteBuffer.allocate(2 + 4 + payload.size)
            .order(ByteOrder.BIG_ENDIAN)
            .putShort(id)
            .putInt(payload.size)
            .put(payload)
            .array()

        logger.debug { "Sending: ${encoding.toAsciiHexString()}" }
        return encoding
    }

    inline fun <reified T : Any> decode(bytes: ByteArray, clazz: KClass<T>): T =
        decodePayload(bytes.copyOfRange(6, bytes.size), clazz)

    inline fun <reified T : Any> decodePayload(payload: ByteArray, clazz: KClass<T>): T {
        return when (clazz) {
            ACL::class -> ProtoBuf.decodeFromByteArray<ACL>(payload) as T
            Authenticate::class -> ProtoBuf.decodeFromByteArray<Authenticate>(payload) as T
            BanList::class -> ProtoBuf.decodeFromByteArray<BanList>(payload) as T
            ChannelRemove::class -> ProtoBuf.decodeFromByteArray<ChannelRemove>(payload) as T
            ChannelState::class -> ProtoBuf.decodeFromByteArray<ChannelState>(payload) as T
            CodecVersion::class -> ProtoBuf.decodeFromByteArray<CodecVersion>(payload) as T
            ContextAction::class -> ProtoBuf.decodeFromByteArray<ContextAction>(payload) as T
            ContextActionModify::class -> ProtoBuf.decodeFromByteArray<ContextActionModify>(payload) as T
            CryptSetup::class -> ProtoBuf.decodeFromByteArray<CryptSetup>(payload) as T
            PermissionDenied::class -> ProtoBuf.decodeFromByteArray<PermissionDenied>(payload) as T
            PermissionQuery::class -> ProtoBuf.decodeFromByteArray<PermissionQuery>(payload) as T
            Ping::class -> ProtoBuf.decodeFromByteArray<Ping>(payload) as T
            QueryUsers::class -> ProtoBuf.decodeFromByteArray<QueryUsers>(payload) as T
            Reject::class -> ProtoBuf.decodeFromByteArray<Reject>(payload) as T
            RequestBlob::class -> ProtoBuf.decodeFromByteArray<RequestBlob>(payload) as T
            ServerConfig::class -> ProtoBuf.decodeFromByteArray<ServerConfig>(payload) as T
            ServerSync::class -> ProtoBuf.decodeFromByteArray<ServerSync>(payload) as T
            SuggestConfig::class -> ProtoBuf.decodeFromByteArray<SuggestConfig>(payload) as T
            TextMessage::class -> ProtoBuf.decodeFromByteArray<TextMessage>(payload) as T
            UDPTunnel::class -> ProtoBuf.decodeFromByteArray<UDPTunnel>(payload) as T
            UserList::class -> ProtoBuf.decodeFromByteArray<UserList>(payload) as T
            UserRemove::class -> ProtoBuf.decodeFromByteArray<UserRemove>(payload) as T
            UserState::class -> ProtoBuf.decodeFromByteArray<UserState>(payload) as T
            UserStats::class -> ProtoBuf.decodeFromByteArray<UserStats>(payload) as T
            Version::class -> ProtoBuf.decodeFromByteArray<Version>(payload) as T
            VoiceTarget::class -> ProtoBuf.decodeFromByteArray<VoiceTarget>(payload) as T
            else -> throw IllegalArgumentException("Unknown type for decoding: ${clazz.simpleName}")
        }
    }
}