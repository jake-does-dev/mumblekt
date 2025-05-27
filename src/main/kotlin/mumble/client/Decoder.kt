package dev.jakedoes.mumble.client

import dev.jakedoes.logging.LogLevel
import dev.jakedoes.logging.log
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
import dev.jakedoes.mumble.domain.UserList
import dev.jakedoes.mumble.domain.UserRemove
import dev.jakedoes.mumble.domain.UserState
import dev.jakedoes.mumble.domain.UserStats
import dev.jakedoes.mumble.domain.Version
import dev.jakedoes.mumble.domain.VoiceTarget
import dev.jakedoes.mumble.protocol.MessageType
import dev.jakedoes.mumble.protocol.MumbleProtocol
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass

private val logger: KLogger = KotlinLogging.logger {  }

internal object Decoder {
    fun decode(messageType: MessageType, payload: ByteArray): Any? =
        when (messageType) {
            MessageType.ACL -> decodeInternal(payload, ACL::class)
            MessageType.Authenticate -> decodeInternal(payload, Authenticate::class)
            MessageType.BanList -> decodeInternal(payload, BanList::class)
            MessageType.ChannelRemove -> decodeInternal(payload, ChannelRemove::class)
            MessageType.ChannelState -> decodeInternal(payload, ChannelState::class)
            MessageType.CodecVersion -> decodeInternal(payload, CodecVersion::class)
            MessageType.ContextAction -> decodeInternal(payload, ContextAction::class)
            MessageType.ContextActionModify -> decodeInternal(payload, ContextActionModify::class)
            MessageType.CryptSetup -> decodeInternal(payload, CryptSetup::class)
            MessageType.PermissionDenied -> decodeInternal(payload, PermissionDenied::class)
            MessageType.PermissionQuery -> decodeInternal(payload, PermissionQuery::class)
            MessageType.Ping -> decodeInternal(payload, Ping::class, LogLevel.Debug)
            MessageType.QueryUsers -> decodeInternal(payload, QueryUsers::class)
            MessageType.Reject -> decodeInternal(payload, Reject::class)
            MessageType.RequestBlob -> decodeInternal(payload, RequestBlob::class)
            MessageType.ServerConfig -> decodeInternal(payload, ServerConfig::class)
            MessageType.ServerSync -> decodeInternal(payload, ServerSync::class)
            MessageType.SuggestConfig -> decodeInternal(payload, SuggestConfig::class)
            MessageType.TextMessage -> decodeInternal(payload, TextMessage::class)
            MessageType.UDPTunnel -> {
                logger.debug { "Dropping UDPTunnel messages, as they're not used in the TCP connection." }
            }
            MessageType.UserList -> decodeInternal(payload, UserList::class)
            MessageType.UserRemove -> decodeInternal(payload, UserRemove::class)
            MessageType.UserState -> decodeInternal(payload, UserState::class)
            MessageType.UserStats -> decodeInternal(payload, UserStats::class)
            MessageType.Version -> decodeInternal(payload, Version::class)
            MessageType.VoiceTarget -> decodeInternal(payload, VoiceTarget::class)
            MessageType.Unrecognised -> {
                logger.warn { "Unknown message type received, ignoring." }
            }
        }

    private inline fun <reified T : Any> decodeInternal(payload: ByteArray, clazz: KClass<T>): T {
        return decodeInternal(payload, clazz, LogLevel.Info)
    }

    private inline fun <reified T : Any> decodeInternal(
        payload: ByteArray, clazz: KClass<T>, logLevel: LogLevel
    ): T {
        val message: T = MumbleProtocol.decodePayload(payload, clazz)
        logger.log(logLevel, "Received: [${message}]")
        return message
    }

}