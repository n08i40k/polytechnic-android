package ru.n08i40k.polytechnic.next.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import ru.n08i40k.polytechnic.next.SettingsV2
import java.io.InputStream
import java.io.OutputStream

object SettingsV2Serializer : Serializer<SettingsV2> {
    override val defaultValue: SettingsV2 = SettingsV2.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingsV2 =
        try {
            SettingsV2.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: SettingsV2, output: OutputStream) = t.writeTo(output)
}

val Context.settings: DataStore<SettingsV2> by dataStore(
    fileName = "settings-v2.pb",
    serializer = SettingsV2Serializer
)