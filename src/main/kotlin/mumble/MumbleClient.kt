package dev.jakedoes.mumble

import dev.jakedoes.logging.LogLevel
import dev.jakedoes.logging.log
import dev.jakedoes.mumble.domain.*
import dev.jakedoes.mumble.protocol.MessageType
import dev.jakedoes.mumble.protocol.MumbleProtocol
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger { }

class MumbleClient(val reader: ByteReadChannel, val writer: ByteWriteChannel, private val socket: Socket) :
    CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO + CoroutineName("MumbleClientScope")

    private var pingJob: Job? = null
    private var readMessagesJob: Job? = null

    companion object {
        suspend fun connect(
            hostname: String,
            port: Int,
            clientCertPath: String,
            serverCertPath: String
        ): MumbleClient {
            val socket = SocketProvider.ssl(hostname, port, clientCertPath, serverCertPath)
            return MumbleClient(socket.openReadChannel(), socket.openWriteChannel(), socket)
        }
    }

    fun startPinging() {
        if (pingJob?.isActive == true) {
            logger.info { "Ping job already active." }
            return
        }
        pingJob =
            launch(CoroutineName("MumbleClient-Pinger")) { // 'launch' is available directly because MumbleClient implements CoroutineScope
                while (isActive) { // This 'isActive' refers to the MumbleClient's coroutineContext
                    ping()
                    delay(15.seconds)
                }
                logger.info { "Ping job stopped." }
            }
    }

    fun startReadingMessages() {
        if (readMessagesJob?.isActive == true) {
            logger.info { "Message reading job already active." }
            return
        }
        readMessagesJob = launch(CoroutineName("MumbleClient-MessageHandler")) {
            while (isActive) {
                val id = ByteBuffer.wrap(reader.readByteArray(2))
                    .order(ByteOrder.BIG_ENDIAN)

                val length = ByteBuffer.wrap(reader.readByteArray(4))
                    .order(ByteOrder.BIG_ENDIAN)

                val payload = ByteBuffer.allocate(length.getInt(0))
                repeat(length.getInt(0)) { payload.put(reader.readByte()) }

                when (MessageType.fromId(id.getShort(0))) {
                    MessageType.ACL -> decode(payload.array(), ACL::class)
                    MessageType.Authenticate -> decode(payload.array(), Authenticate::class)
                    MessageType.BanList -> decode(payload.array(), BanList::class)
                    MessageType.ChannelRemove -> decode(payload.array(), ChannelRemove::class)
                    MessageType.ChannelState -> decode(payload.array(), ChannelState::class)
                    MessageType.CodecVersion -> decode(payload.array(), CodecVersion::class)
                    MessageType.ContextAction -> decode(payload.array(), ContextAction::class)
                    MessageType.ContextActionModify -> decode(payload.array(), ContextActionModify::class)
                    MessageType.CryptSetup -> decode(payload.array(), CryptSetup::class)
                    MessageType.PermissionDenied -> decode(payload.array(), PermissionDenied::class)
                    MessageType.PermissionQuery -> decode(payload.array(), PermissionQuery::class)
                    MessageType.Ping -> decode(payload.array(), Ping::class, LogLevel.Debug)
                    MessageType.QueryUsers -> decode(payload.array(), QueryUsers::class)
                    MessageType.Reject -> decode(payload.array(), Reject::class)
                    MessageType.RequestBlob -> decode(payload.array(), RequestBlob::class)
                    MessageType.ServerConfig -> decode(payload.array(), ServerConfig::class)
                    MessageType.ServerSync -> decode(payload.array(), ServerSync::class)
                    MessageType.SuggestConfig -> decode(payload.array(), SuggestConfig::class)
                    MessageType.TextMessage -> {
                        val receivedMessage = decode(payload.array(), TextMessage::class)
                        writer.writeByteArray(
                            MumbleProtocol.encode(
                                TextMessage(message = "Hello! You said: ${receivedMessage.message}", treeId = listOf(0))
                            )
                        )
                        writer.flush()
                    }
                    MessageType.UDPTunnel -> {
                        logger.debug {"Dropping UDPTunnel messages, as they're not used in the TCP connection." }
                    }
                    MessageType.UserList -> decode(payload.array(), UserList::class)
                    MessageType.UserRemove -> decode(payload.array(), UserRemove::class)
                    MessageType.UserState -> decode(payload.array(), UserState::class)
                    MessageType.UserStats -> decode(payload.array(), UserStats::class)
                    MessageType.Version -> decode(payload.array(), Version::class)
                    MessageType.VoiceTarget -> decode(payload.array(), VoiceTarget::class)
                    else -> logger.warn { "Unknown message type received" }
                }
            }
        }
    }

    suspend fun handshake(major: Long, minor: Long, username: String, password: String) {
        writer.writeByteArray(
            MumbleProtocol.encode(
                Version(
                    major = major,
                    minor = minor,
                    os = "linux",
                    osVersion = "mumblekt",
                    release = "1.5.0",
                )
            )
        )
        writer.flush()

        writer.writeByteArray(MumbleProtocol.encode(Authenticate(username, password)))
        writer.flush()
    }

    suspend fun ping() {
        writer.writeByteArray(MumbleProtocol.encode(Ping()))
        writer.flush()
        logger.debug { "Sent ping!" }
    }

    private inline fun <reified T : Any> decode(payload: ByteArray, clazz: KClass<T>): T =
        decode(payload, clazz, LogLevel.Info)

    private inline fun <reified T : Any> decode(payload: ByteArray, clazz: KClass<T>, logLevel: LogLevel): T {
        val message: T = MumbleProtocol.decodePayload(payload, clazz)
        logger.log(logLevel, "Received: [${message}]")
        return message
    }
}