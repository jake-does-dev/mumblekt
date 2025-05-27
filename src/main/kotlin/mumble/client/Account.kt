package dev.jakedoes.mumble.client

sealed interface Account {
    val session: Int?
    val channelId: Int?
    val name: String?

    data class User(
        override val session: Int?,
        override val channelId: Int?,
        override val name: String?,
    ) : Account

    data class Bot(
        override val session: Int?,
        override val channelId: Int?,
        override val name: String?,
    ) : Account
}
