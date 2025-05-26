package dev.jakedoes

import dev.jakedoes.mumble.MumbleClient
import io.github.oshai.kotlinlogging.KotlinLogging
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
    client.handshake(1L, 5L, "jake-does-testing", password)
    client.startPinging()
    client.startReadingMessages()
}