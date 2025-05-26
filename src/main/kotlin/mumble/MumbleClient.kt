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

    /**
     * Registers a callback function to be invoked when a message of the specified type is received.
     *
     * @param T The type of the message to listen for.
     * @param clazz The KClass of the message type (e.g., `TextMessage::class`).
     * @param callback The suspend function to call when the message is received. It will receive the decoded message as an argument.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> on(clazz: KClass<T>, callback: MessageCallback<T>) {
        // We need to cast the callback to MessageCallback<*> to store it in the map
        // This cast is safe because we'll ensure type safety when retrieving and invoking.
        val callbacksForType = messageCallbacks.getOrPut(clazz) { mutableListOf() } as MutableList<MessageCallback<Any>>
        callbacksForType.add(callback as MessageCallback<Any>) // Cast back to Any for storage
        logger.debug { "Registered callback for message type: ${clazz.simpleName}" }
    }

    // This allows you to set up callbacks easily when creating the client.
    fun init(initBlock: MumbleClient.() -> Unit): MumbleClient {
        initBlock()
        startPinging()
        startReadingMessages()
        return this
    }


    fun startPinging() {
        if (pingJob?.isActive == true) {
            logger.info { "Ping job already active." }
            return
        }
        pingJob = launch(CoroutineName("MumbleClient-Pinger")) {
            while (isActive) {
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

                // --- NEW: Invoke Callbacks ---
                if (decodedMessage != null) {
                    val clazz = decodedMessage::class
                    val callbacks = messageCallbacks[clazz]
                    if (callbacks != null) {
                        for (callback in callbacks) {
                            try {
                                // Safe cast because we store callbacks in the map based on their KClass
                                @Suppress("UNCHECKED_CAST")
                                val typedCallback = callback as MessageCallback<Any>
                                typedCallback(decodedMessage) // Invoke the callback
                            } catch (e: Exception) {
                                logger.error(e) { "Error executing callback for message type ${clazz.simpleName}" }
                            }
                        }
                    }
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
    }

    // Renamed 'decode' to 'decodeMessageInternal' to avoid confusion with the public `on` function
    // and to indicate it's an internal helper that also logs.
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