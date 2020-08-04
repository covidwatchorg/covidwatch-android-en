package org.covidwatch.android.data.pref

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.internal.JavaVersion
import com.google.gson.internal.PreJava9DateFormatProvider
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.text.DateFormat
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.*

fun gsonWithInstantAdapter(): Gson =
    GsonBuilder().registerTypeAdapter(Instant::class.java, InstantTypeAdapter()).create()

class InstantTypeAdapter : TypeAdapter<Instant?>() {
    private val dateFormats = mutableListOf<DateFormat>()

    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): Instant? {
        return if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            null
        } else {
            deserializeToDate(jsonReader.nextString())
        }
    }

    @Synchronized
    private fun deserializeToDate(json: String): Instant {
        try {
            return Instant.parse(json)
        } catch (_: DateTimeParseException) {
        }

        return dateFormats.map { dateFormat ->
            try {
                dateFormat.parse(json)?.toInstant()
            } catch (e: Exception) {
                null
            }
        }.firstOrNull() ?: throw JsonSyntaxException(json)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun write(
        out: JsonWriter,
        value: Instant?
    ) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    init {
        dateFormats.add(
            DateFormat.getDateTimeInstance(
                2,
                2,
                Locale.US
            )
        )
        if (Locale.getDefault() != Locale.US) {
            dateFormats.add(DateFormat.getDateTimeInstance(2, 2))
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(
                PreJava9DateFormatProvider.getUSDateTimeFormat(
                    2,
                    2
                )
            )
        }
    }
}