package dev.jakedoes.mumble.protocol

internal enum class MessageType(val id: Short) {
    Version(0),
    UDPTunnel(1),
    Authenticate(2),
    Ping(3),
    Reject(4),
    ServerSync(5),
    ChannelRemove(6),
    ChannelState(7),
    UserRemove(8),
    UserState(9),
    BanList(10),
    TextMessage(11),
    PermissionDenied(12),
    ACL(13),
    QueryUsers(14),
    CryptSetup(15),
    ContextActionModify(16),
    ContextAction(17),
    UserList(18),
    VoiceTarget(19),
    PermissionQuery(20),
    CodecVersion(21),
    UserStats(22),
    RequestBlob(23),
    ServerConfig(24),
    SuggestConfig(25),
    Unrecognised(-1);

    companion object {
        private val map = MessageType.entries.associateBy(MessageType::id)

        fun fromId(id: Short): MessageType? =
            map[id]
    }
}