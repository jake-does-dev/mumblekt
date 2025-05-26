package dev.jakedoes

import dev.jakedoes.mumble.SocketProvider
import dev.jakedoes.mumble.domain.Authenticate
import dev.jakedoes.mumble.domain.Version
import dev.jakedoes.mumble.protocol.MessageType
import dev.jakedoes.mumble.protocol.MumbleProtocol
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

private val logger = KotlinLogging.logger {  }

// ./gradlew run --args "localhost 64738 mumble-client-cert.p12 mumble-server-cert.pem"
suspend fun main(args: Array<String>) {
    assert(args.size == 5) { "Expected four arguments in my array" }
    val (hostname, port, password, clientCertPath, serverCertPath) = args

    /*
    Notes:
    - the client cert created from Mumble's 'Certificate Wizard' has no password, and no alias.
    - I used `openssl s_client -connect localhost:64738 -showcerts` to get the server-side cert
     */

    val socket = SocketProvider.ssl(hostname, port.toInt(), clientCertPath, serverCertPath)
    val writer = socket.openWriteChannel()
    val reader = socket.openReadChannel()

    connect(writer, password)

    while(true) {
        val id: Short = ByteBuffer.wrap(reader.readByteArray(2))
            .order(ByteOrder.BIG_ENDIAN)
            .short

        val length = ByteBuffer.wrap(reader.readByteArray(4))
            .order(ByteOrder.BIG_ENDIAN)
            .int

        when (MessageType.fromId(id)) {
            MessageType.TextMessage -> logger.info { "Received a text message!" }
            else -> logger.info { "Received a different message!" }
        }

        repeat(length) { reader.readByte() }
    }
}

private suspend fun connect(writer: ByteWriteChannel, password: String) {
    writer.writeByteArray(
        MumbleProtocol.encode(
            Version(
                major = 1L,
                minor = 5L,
                os = "Linux",
                osVersion = "Fedora KDE 42",
                release = "1.5.0",
            )
        )
    )
    writer.flush()

    writer.writeByteArray(
        MumbleProtocol.encode(
            Authenticate(
                "jake-does-testing", password
            )
        )
    )
    writer.flush()
}