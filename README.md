# MumbleKt

A Mumble wrapper, written in Kotlin.

## Prerequisites

---
### Client-side certificate

Using a Mumble client, go through the Certificate Wizard to save off a `.p12` client-side cert.
Remember the path.

![certificate-wizard](assets/get-client-cert.png)

### Server-side certificate

Obtain the public facing server-side cert, via `openssl`, replacing `localhost` with the Mumble server you
are connecting to:

```bash
openssl s_client -connect localhost:64738 -showcerts
```
Navigate the output.

Save the entire chain of `-----BEGIN CERTIFICATE-----`, `-----END CERTIFICATE-----` as a `.pem` file.

---

## Usage

```kotlin
MumbleClient.connect(
    hostname = "localhost",
    port = 64738,
    clientCertPath = "/path/to/client/cert",
    serverCertPath = "/path/to/server/cert",
    username = "mumblekt",
    password = password // if the server you're connecting to has password based auth
)
    .init {
        // define callbacks to run when different message types are received
        on(TextMessage::class) { textMessage ->
            // writes a packet adhering to the TCP protocol
            write(TextMessage(message = "Hello! You said: ${textMessage.message}", channelId = textMessage.channelId))
        }

        // you can define multiple callbacks for the same message type
        on(TextMessage::class) { textMessage ->
            logger.info { "Another TextMessage handler: '${textMessage.message}'" }
        }

        on(UserState::class) { userState ->
            logger.info {
                ">>>> UserState Update: ${userState.name} (Session: ${userState.session}) is in channel ${
                    findChannelName(
                        userState.name
                    )
                }"
            }
        }

        on(Ping::class) { ping ->
            logger.debug { ">>>> Received Ping: ${ping.good} good packets, ${ping.lost} lost" }
        }
    }
```