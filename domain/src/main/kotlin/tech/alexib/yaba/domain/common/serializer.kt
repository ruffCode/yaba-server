package tech.alexib.yaba.domain.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.OffsetDateTime
import java.util.UUID
import java.time.LocalDate as JavaLocalDate
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)


    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())


    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = java.time.LocalDate::class)
object JavaLocalDateSerializer :KSerializer<JavaLocalDate>{
    override fun deserialize(decoder: Decoder): JavaLocalDate = JavaLocalDate.parse(decoder.decodeString())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JavaLocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: JavaLocalDate) = encoder.encodeString(value.toString())
}
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
}
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = OffsetDateTime::class)
object OffsetDateTimeSerializer:KSerializer<OffsetDateTime>{
    override fun deserialize(decoder: Decoder): OffsetDateTime =
        OffsetDateTime.parse(decoder.decodeString())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }
}

val serializerModule = SerializersModule {
    contextual(InstantSerializer)
    contextual(UUIDSerializer)
    contextual(LocalDateSerializer)
    contextual(OffsetDateTimeSerializer)
    contextual(JavaLocalDateSerializer)

}

val jSerializer = Json {
    serializersModule = serializerModule
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
//    prettyPrint = true
}
