package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
enum class RejectType {
    None,
    WrongVersion,
    InvalidUsername,
    WrongUserPW,
    WrongServerPW,
    UsernameInUse,
    ServerFull,
    NoCertificate,
    AuthenticatorFail,
    NoNewConnections
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Reject(
    @ProtoNumber(1) val type: RejectType? = null,
    @ProtoNumber(2) val reason: String? = null
)
