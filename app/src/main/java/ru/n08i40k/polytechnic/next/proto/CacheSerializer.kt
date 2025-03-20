package ru.n08i40k.polytechnic.next.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import ru.n08i40k.polytechnic.next.Cache
import java.io.InputStream
import java.io.OutputStream

object CacheSerializer : Serializer<Cache> {
    override val defaultValue: Cache = Cache.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Cache =
        try {
            Cache.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: Cache, output: OutputStream) = t.writeTo(output)
}

val Context.cache: DataStore<Cache> by dataStore(
    fileName = "cache.pb",
    serializer = CacheSerializer
)