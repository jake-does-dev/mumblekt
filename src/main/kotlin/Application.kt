package dev.jakedoes

import dev.jakedoes.mumble.SocketProvider
import dev.jakedoes.mumble.domain.Authenticate
import dev.jakedoes.mumble.domain.Version
import dev.jakedoes.mumble.protocol.MumbleProtocol
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers

// ./gradlew run --args "localhost 64738 mumble-client-cert.p12 mumble-server-cert.pem"
suspend fun main(args: Array<String>) {
    assert(args.size == 4) { "Expected four arguments in my array" }
    val (hostname, port, clientCertPath, serverCertPath) = args
    val selectorManager = ActorSelectorManager(Dispatchers.IO)

    /*
    Notes:
    - the client cert created from Mumble's 'Certificate Wizard' has no password, and no alias.
    - I used `openssl s_client -connect localhost:64738 -showcerts` to get the server-side cert
     */

    val socket = SocketProvider.ssl(hostname, port.toInt(), clientCertPath, serverCertPath)
    val writer = socket.openWriteChannel()
    val reader = socket.openReadChannel()

    writer.writeByteArray(
        MumbleProtocol.encode(
            Version(
                release = "mumblekt"
            )
        )
    )
    writer.flush()

    writer.writeByteArray(
        MumbleProtocol.encode(
            Authenticate(
                "jake-does-testing", ""
            )
        )
    )
    writer.flush()

    Thread.sleep(30L * 1000 * 1000)
}