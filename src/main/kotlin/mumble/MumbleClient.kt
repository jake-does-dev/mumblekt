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

typealias MessageCallback<T> = suspend (T) -> Unit

class MumbleClient(val reader: ByteReadChannel, val writer: ByteWriteChannel, private val socket: Socket) :
    CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO + CoroutineName("MumbleClientScope")

    private var pingJob: Job? = null
    private var readMessagesJob: Job? = null

    private val messageCallbacks = mutableMapOf<KClass<*>, MutableList<MessageCallback<*>>>()

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

    suspend fun handshake(major: Long, minor: Long, username: String, password: String) {
        write(Version(major, minor, "linux", "mumblekt", "1.5.0"))
        write(Authenticate(username, password))
    }

    fun init(initBlock: MumbleClient.() -> Unit): MumbleClient {
        initBlock()
        startPinging()
        startReadingMessages()
        return this
    }

    /**
     * Registers a callback function, e.g.,
     * ```
     * MumbleClient.connect(...).init {
     *     on(TextMessage::class) { message ->
     *         // do stuff with the `message: TestMessage`
     *     }
     *     on(Ping::class) { message ->
     *         // do stuff with the `message: Ping`
     *     }
     * }
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> on(clazz: KClass<T>, callback: MessageCallback<T>) {
        val callbacksForType = messageCallbacks.getOrPut(clazz) { mutableListOf() } as MutableList<MessageCallback<Any>>
        callbacksForType.add(callback as MessageCallback<Any>)
        logger.debug { "Registered callback for message type: ${clazz.simpleName}" }
    }

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

    private fun startPinging() {
        if (pingJob?.isActive == true) {
            logger.info { "Ping job already active." }
            return
        }
        pingJob = launch(CoroutineName("MumbleClient-Pinger")) {
            while (isActive) {
                write(Ping())
                delay(15.seconds)
            }
            logger.info { "Ping job stopped." }
        }
    }

    private fun startReadingMessages() {
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
                val payloadArray = payload.array()

                val messageType = MessageType.fromId(id.getShort(0))
                val decodedMessage: Any? = when (messageType) {
                    MessageType.ACL -> decodeInternal(payloadArray, ACL::class)
                    MessageType.Authenticate -> decodeInternal(payloadArray, Authenticate::class)
                    MessageType.BanList -> decodeInternal(payloadArray, BanList::class)
                    MessageType.ChannelRemove -> decodeInternal(payloadArray, ChannelRemove::class)
                    MessageType.ChannelState -> decodeInternal(payloadArray, ChannelState::class)
                    MessageType.CodecVersion -> decodeInternal(payloadArray, CodecVersion::class)
                    MessageType.ContextAction -> decodeInternal(payloadArray, ContextAction::class)
                    MessageType.ContextActionModify -> decodeInternal(payloadArray, ContextActionModify::class)
                    MessageType.CryptSetup -> decodeInternal(payloadArray, CryptSetup::class)
                    MessageType.PermissionDenied -> decodeInternal(payloadArray, PermissionDenied::class)
                    MessageType.PermissionQuery -> decodeInternal(payloadArray, PermissionQuery::class)
                    MessageType.Ping -> decodeInternal(payloadArray, Ping::class, LogLevel.Debug)
                    MessageType.QueryUsers -> decodeInternal(payloadArray, QueryUsers::class)
                    MessageType.Reject -> decodeInternal(payloadArray, Reject::class)
                    MessageType.RequestBlob -> decodeInternal(payloadArray, RequestBlob::class)
                    MessageType.ServerConfig -> decodeInternal(payloadArray, ServerConfig::class)
                    MessageType.ServerSync -> decodeInternal(payloadArray, ServerSync::class)
                    MessageType.SuggestConfig -> decodeInternal(payloadArray, SuggestConfig::class)
                    MessageType.TextMessage -> decodeInternal(payloadArray, TextMessage::class)
                    MessageType.UDPTunnel -> {
                        logger.debug { "Dropping UDPTunnel messages, as they're not used in the TCP connection." }
                    }

                    MessageType.UserList -> decodeInternal(payloadArray, UserList::class)
                    MessageType.UserRemove -> decodeInternal(payloadArray, UserRemove::class)
                    MessageType.UserState -> decodeInternal(payloadArray, UserState::class)
                    MessageType.UserStats -> decodeInternal(payloadArray, UserStats::class)
                    MessageType.Version -> decodeInternal(payloadArray, Version::class)
                    MessageType.VoiceTarget -> decodeInternal(payloadArray, VoiceTarget::class)
                    else -> {
                        logger.warn { "Unknown message type received: $messageType" }
                        null
                    }
                }

                if (decodedMessage != null) {
                    val clazz = decodedMessage::class
                    val callbacks = messageCallbacks[clazz]
                    if (callbacks != null) {
                        for (callback in callbacks) {
                            try {
                                // Safe cast because we store callbacks in the map based on their KClass
                                @Suppress("UNCHECKED_CAST")
                                val typedCallback = callback as MessageCallback<Any>
                                typedCallback(decodedMessage)
                            } catch (e: Exception) {
                                logger.error(e) { "Error executing callback for message type ${clazz.simpleName}" }
                            }
                        }
                    }
                }
            }
        }
    }

    private inline fun <reified T : Any> decodeInternal(payload: ByteArray, clazz: KClass<T>): T {
        return decodeInternal(payload, clazz, LogLevel.Info)
    }

    private inline fun <reified T : Any> decodeInternal(
        payload: ByteArray,
        clazz: KClass<T>,
        logLevel: LogLevel
    ): T {
        val message: T = MumbleProtocol.decodePayload(payload, clazz)
        logger.log(logLevel, "Received: [${message}]")
        return message
    }
}