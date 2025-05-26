package dev.jakedoes.mumble.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber


@Serializable(with = ContextActionModifyContextSerializer::class)
enum class ContextActionModifyContext(val protoValue: Int) {
    Server(0x01),
    Channel(0x02),
    User(0x04);

    companion object {
        private val map = entries.associateBy(ContextActionModifyContext::protoValue)
        fun fromProtoValue(value: Int) = map[value]
    }


    operator fun plus(other: ContextActionModifyContext): Int = this.protoValue or other.protoValue
    infix fun or(other: ContextActionModifyContext): Int = this.protoValue or other.protoValue
}

object ContextActionModifyContextSerializer : KSerializer<ContextActionModifyContext> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ContextActionModifyContext", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: ContextActionModifyContext) {
        encoder.encodeInt(value.protoValue)
    }

    override fun deserialize(decoder: Decoder): ContextActionModifyContext {
        val protoValue = decoder.decodeInt()
        return ContextActionModifyContext.fromProtoValue(protoValue)
            ?: throw IllegalArgumentException("Unknown ContextActionModifyContext value: $protoValue")
    }
}


@Serializable(with = ContextActionModifyOperationSerializer::class)
enum class ContextActionModifyOperation(val protoValue: Int) {
    Add(0),
    Remove(1);

    companion object {
        private val map = entries.associateBy(ContextActionModifyOperation::protoValue)
        fun fromProtoValue(value: Int) = map[value]
    }
}

object ContextActionModifyOperationSerializer : KSerializer<ContextActionModifyOperation> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ContextActionModifyOperation", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: ContextActionModifyOperation) {
        encoder.encodeInt(value.protoValue)
    }

    override fun deserialize(decoder: Decoder): ContextActionModifyOperation {
        val protoValue = decoder.decodeInt()
        return ContextActionModifyOperation.fromProtoValue(protoValue)
            ?: throw IllegalArgumentException("Unknown ContextActionModifyOperation value: $protoValue")
    }
}


@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ContextActionModify(
    @ProtoNumber(1) val action: String,
    @ProtoNumber(2) val text: String? = null,
    @ProtoNumber(3) val context: Long? = null,
    @ProtoNumber(4) val operation: ContextActionModifyOperation? = null
)