package dev.jakedoes

import dev.jakedoes.mumble.MumbleClient
import dev.jakedoes.mumble.domain.Ping
import dev.jakedoes.mumble.domain.TextMessage
import dev.jakedoes.mumble.domain.UserState
import dev.jakedoes.mumble.protocol.MumbleProtocol
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.writeByteArray
import kotlinx.coroutines.runBlocking

private val logger = KotlinLogging.logger { }

// ./gradlew run --args "localhost 64738 '' mumble-client-cert.p12 mumble-server-cert.pem"
fun main(args: Array<String>) = runBlocking {
    assert(args.size == 5) { "Expected four arguments in my array" }
    val (hostname, port, password, clientCertPath, serverCertPath) = args

    /*
    Notes:
    - the client cert created from Mumble's 'Certificate Wizard' has no password, and no alias.
    - I used `openssl s_client -connect localhost:64738 -showcerts` to get the server-side cert
     */
    val client = MumbleClient.connect(hostname, port.toInt(), clientCertPath, serverCertPath)
        .init {
            on(TextMessage::class) { message ->
                logger.info { "Received ${message}, about to echo it back... "}
                writer.writeByteArray(
                    MumbleProtocol.encode(
                        TextMessage(message = "Hello! You said: ${message.message}", treeId = listOf(0))
                    )
                )
                writer.flush()
            }

            // Register a callback for UserState
            on(UserState::class) { userState ->
                logger.info { ">>>> UserState Update: ${userState.name} (Session: ${userState.session}) is in channel ${userState.channelId}" }
            }

            // Register a callback for Ping messages (if you want to react to them)
            on(Ping::class) { ping ->
                logger.debug { ">>>> Received Ping: ${ping.good} good packets, ${ping.lost} lost" }
            }

            // You can register multiple callbacks for the same type
            on(TextMessage::class) { textMessage ->
                logger.info { "Another TextMessage handler: '${textMessage.message}'" }
            }

        }
    client.handshake(1L, 5L, "jake-does-testing", password)
//    client.startPinging()
//    client.startReadingMessages()
}