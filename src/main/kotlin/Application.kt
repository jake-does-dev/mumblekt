package dev.jakedoes

import dev.jakedoes.mumble.domain.Message.Authenticate
import dev.jakedoes.mumble.domain.Message.Version
import dev.jakedoes.mumble.protocol.MumbleProtocol
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.tls.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.coroutineContext


suspend fun main(args: Array<String>) {
    assert(args.size == 4) { "Expected four arguments in my array" }
    // first argument is hostname, second argument is port
    val (hostname, port, clientCertPath, serverCertPath) = args
    val selectorManager = ActorSelectorManager(Dispatchers.IO)

    /*
    Notes:
    - the client cert created from Mumble's 'Certificate Wizard' has no password, and no alias.
    - I used `openssl s_client -connect localhost:64738 -showcerts` to get the server-side cert
     */

    // --- PREPARE TRUST MANAGER (for server validation) ---
    val customTrustStore = KeyStore.getInstance("PKCS12") // Or "JKS"
    customTrustStore.load(null, null) // Initialize empty in-memory trust store

    val serverCert: X509Certificate?
    FileInputStream(serverCertPath).use { fis ->
        serverCert = CertificateFactory.getInstance("X509").generateCertificate(fis) as X509Certificate
    }

    if (serverCert != null) {
        customTrustStore.setCertificateEntry("mumble-server-cert", serverCert)
        println("Mumble server certificate added to custom trust store.")
    } else {
        throw IllegalStateException("Failed to load Mumble server certificate from $serverCertPath")
    }

    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    tmf.init(customTrustStore)
    val customTrustManager = tmf.trustManagers.filterIsInstance<X509TrustManager>().firstOrNull()
        ?: throw IllegalStateException("No X509TrustManager found in factory for server trust.")
    // --- END PREPARE TRUST MANAGER ---

    val socket = aSocket(selectorManager)
        .tcp()
        .connect(hostname, port = port.toInt())
        .tls(coroutineContext = coroutineContext) {
            trustManager = customTrustManager
        // 1. Load your PKCS#12 file into a KeyStore (for client authentication)
            val clientKeyStore = KeyStore.getInstance("PKCS12")
            FileInputStream(clientCertPath).use { fis ->
                clientKeyStore.load(fis, null)
            }
            println("Client KeyStore loaded successfully.")

            // Add this KeyStore to the TLS configuration for client authentication.
            addKeyStore(
                store = clientKeyStore,
                password = null,
                alias = null // Use null if no friendlyName alias is found in .p12
            )
            println("Client certificate and key configured for TLS.")
        }

    val writer = socket.openWriteChannel()
    val reader = socket.openReadChannel()

    writer.writeByteArray(
        MumbleProtocol.encode(
            Version(
                1, 5, "mumblekt", "Fedora KDE Plasma", "42"
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