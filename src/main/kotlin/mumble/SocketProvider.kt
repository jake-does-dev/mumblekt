package dev.jakedoes.mumble

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.tls.*
import kotlinx.coroutines.Dispatchers
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.coroutineContext

private val logger = KotlinLogging.logger {  }

object SocketProvider {
    suspend fun ssl(hostname: String, port: Int, clientP12Path: String, serverPemPath: String): Socket {
        val selectorManager = ActorSelectorManager(Dispatchers.IO)

        val customTrustStore = KeyStore.getInstance("PKCS12")
        customTrustStore.load(null, null) // Initialize empty in-memory trust store

        val serverCert: X509Certificate?
        FileInputStream(serverPemPath).use { fis ->
            serverCert = CertificateFactory.getInstance("X509").generateCertificate(fis) as X509Certificate
        }

        if (serverCert != null) {
            customTrustStore.setCertificateEntry("mumble-server-cert", serverCert)
            logger.info { "Mumble server certificate added to custom trust store." }
        } else {
            throw IllegalStateException("Failed to load Mumble server certificate from $clientP12Path")
        }

        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(customTrustStore)
        val customTrustManager = tmf.trustManagers.filterIsInstance<X509TrustManager>().firstOrNull()
            ?: throw IllegalStateException("No X509TrustManager found in factory for server trust.")

        return aSocket(selectorManager)
            .tcp()
            .connect(hostname, port = port)
            .tls(coroutineContext = coroutineContext) {
                trustManager = customTrustManager

                val clientKeyStore = KeyStore.getInstance("PKCS12")
                FileInputStream(clientP12Path).use { fis ->
                    clientKeyStore.load(fis, null)
                }
                logger.info { "Client KeyStore loaded successfully." }

                addKeyStore(
                    store = clientKeyStore,
                    password = null,
                    alias = null // null as there is no friendlyName alias is found in .p12
                )
                logger.info { "Client certificate and key configured for TLS." }
            }
    }
}