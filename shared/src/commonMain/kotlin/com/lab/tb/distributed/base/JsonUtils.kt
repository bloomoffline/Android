
package com.lab.tb.distributed.base

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

class JsonUtils {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            encodeDefaults = true
            explicitNulls = false
            serializersModule = SerializersModule {
                contextual(AnySerializer)
            }
        }

        inline fun<reified T> encodeToString(value: T): String {
            return json.encodeToString(value)
        }

        inline fun<reified T> decodeFromString(value: String): T {
            return json.decodeFromString(value)
        }

    }

    object AnySerializer : KSerializer<Any> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("AnySerializer", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Any) {
            error("Unsupported")
        }

        override fun deserialize(decoder: Decoder): Any {
            val input = decoder as JsonDecoder
            val element = input.decodeJsonElement()
            return element.toString()
        }
    }

}