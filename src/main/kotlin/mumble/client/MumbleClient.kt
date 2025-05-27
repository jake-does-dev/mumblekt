package dev.jakedoes.mumble.client

import dev.jakedoes.mumble.SocketProvider
import dev.jakedoes.mumble.domain.*
import dev.jakedoes.mumble.protocol.MessageType
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

class MumbleClient : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO + CoroutineName("MumbleClientScope")

    private var pingJob: Job? = null
    private var readMessagesJob: Job? = null
    private val messageCallbacks = mutableMapOf<KClass<*>, MutableList<MessageCallback<*>>>()

    private lateinit var reader: ByteReadChannel
    private lateinit var writer: Writer
    private val channels: MutableSet<Channel> = mutableSetOf()
    private val accounts: MutableSet<Account> = mutableSetOf()

    companion object {
        suspend fun connect(
            hostname: String,
            port: Int,
            clientCertPath: String,
            serverCertPath: String,
            username: String,
            password: String
        ): MumbleClient = connect(hostname, port, clientCertPath, serverCertPath, 1L, 5L, username, password)

        suspend fun connect(
            hostname: String,
            port: Int,
            clientCertPath: String,
            serverCertPath: String,
            major: Long,
            minor: Long,
            username: String,
            password: String
        ): MumbleClient {
            val socket = SocketProvider.ssl(hostname, port, clientCertPath, serverCertPath)
            val client = MumbleClient()
            client.reader = socket.openReadChannel()
            client.writer = Writer(socket.openWriteChannel())
            // handshake
            client.write(Version(major, minor, "linux", "mumblekt", "${major}.${minor}.0"))
            client.write(Authenticate(username, password))
            return client
        }
    }

    suspend fun <T : Any> write(message: T) {
        when (message) {
            is ACL -> writer.write(message)
            is Authenticate -> writer.write(message)
            is BanList -> writer.write(message)
            is ChannelRemove -> writer.write(message)
            is ChannelState -> writer.write(message)
            is CodecVersion -> writer.write(message)
            is ContextAction -> writer.write(message)
            is ContextActionModify -> writer.write(message)
            is CryptSetup -> writer.write(message)
            is PermissionDenied -> writer.write(message)
            is PermissionQuery -> writer.write(message)
            is Ping -> writer.write(message)
            is QueryUsers -> writer.write(message)
            is Reject -> writer.write(message)
            is RequestBlob -> writer.write(message)
            is ServerConfig -> writer.write(message)
            is ServerSync -> writer.write(message)
            is SuggestConfig -> writer.write(message)
            is TextMessage -> writer.write(message)
            is UDPTunnel -> writer.write(message)
            is UserList -> writer.write(message)
            is UserRemove -> writer.write(message)
            is UserState -> writer.write(message)
            is UserStats -> writer.write(message)
            is Version -> writer.write(message)
            is VoiceTarget -> writer.write(message)
            else -> IllegalArgumentException("Unknown message type: $message")
        }
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

    fun findChannelId(username: String?): Int? =
        accounts.first { it.name == username }.channelId

    fun findChannelName(username: String?): String? =
        channels.firstOrNull {
            it.channelId == findChannelId(username)
        }?.name

    private fun startPinging() {
        if (pingJob?.isActive == true) {
            logger.info { "Ping job already active." }
            return
        }
        pingJob = launch(CoroutineName("MumbleClient-Pinger")) {
            while (isActive) {
                writer.write(Ping())
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
                val id = ByteBuffer.wrap(reader.readByteArray(2)).order(ByteOrder.BIG_ENDIAN)
                val length = ByteBuffer.wrap(reader.readByteArray(4)).order(ByteOrder.BIG_ENDIAN)
                val payload = ByteBuffer.allocate(length.getInt(0))
                repeat(length.getInt(0)) { payload.put(reader.readByte()) }
                val payloadArray = payload.array()

                val messageType = MessageType.fromId(id.getShort(0)) ?: MessageType.Unrecognised
                val decoded = Decoder.decode(messageType, payloadArray)
                when (decoded) {
                    is ChannelState -> { channels.add(Channel(decoded.channelId, decoded.name)) }
                    is UserState -> {
                        val session = decoded.session
                        val name = decoded.name
                        val channelId = decoded.channelId ?: 0

                        when {
                            name?.contains("Bot") == true -> accounts.add(Account.Bot(session, channelId, name))
                            name?.contains("bot") == true -> accounts.add(Account.Bot(session, channelId, name))
                            else -> accounts.add(Account.User(session, channelId, name))
                        }
                    }
                }

                if (decoded != null) {
                    val clazz = decoded::class
                    val callbacks = messageCallbacks[clazz]
                    if (callbacks != null) {
                        for (callback in callbacks) {
                            try {
                                // Safe cast because we store callbacks in the map based on their KClass
                                @Suppress("UNCHECKED_CAST") val typedCallback = callback as MessageCallback<Any>
                                typedCallback(decoded)
                            } catch (e: Exception) {
                                logger.error(e) { "Error executing callback for message type ${clazz.simpleName}" }
                            }
                        }
                    }
                }
            }
        }
    }
}