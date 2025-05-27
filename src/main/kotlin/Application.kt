package dev.jakedoes

import dev.jakedoes.mumble.client.MumbleClient
import dev.jakedoes.mumble.domain.Ping
import dev.jakedoes.mumble.domain.TextMessage
import dev.jakedoes.mumble.domain.UserState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking

private val logger = KotlinLogging.logger { }

// ./gradlew run --args "localhost 64738 '' mumble-client-cert.p12 mumble-server-cert.pem"
fun main(args: Array<String>): Unit = runBlocking {
    assert(args.size == 5) { "Expected four arguments in my array" }
    val (hostname, port, password, clientCertPath, serverCertPath) = args
    /*
    Notes:
    - the client cert created from Mumble's 'Certificate Wizard' has no password, and no alias.
    - I used `openssl s_client -connect localhost:64738 -showcerts` to get the server-side cert
     */
    MumbleClient.connect(hostname, port.toInt(), clientCertPath, serverCertPath, "mumblekt", password)
        .init {
            on(TextMessage::class) { message ->
                write(TextMessage(message = "Hello! You said: ${message.message}", channelId = message.channelId))
            }

            on(UserState::class) { userState ->
                logger.info { ">>>> UserState Update: ${userState.name} (Session: ${userState.session}) is in channel ${findChannelName(userState.name)}" }
            }

            on(Ping::class) { ping ->
                logger.debug { ">>>> Received Ping: ${ping.good} good packets, ${ping.lost} lost" }
            }

            on(TextMessage::class) { textMessage ->
                logger.info { "Another TextMessage handler: '${textMessage.message}'" }
            }
        }
}