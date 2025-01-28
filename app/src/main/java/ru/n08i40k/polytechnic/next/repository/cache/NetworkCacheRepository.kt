package ru.n08i40k.polytechnic.next.repository.cache

import ru.n08i40k.polytechnic.next.CachedResponse
import ru.n08i40k.polytechnic.next.UpdateDates

interface NetworkCacheRepository {
    suspend fun put(url: String, data: String)

    suspend fun get(url: String): CachedResponse?

    suspend fun clear()

    suspend fun isHashPresent(): Boolean

    suspend fun setHash(hash: String)

    suspend fun getUpdateDates(): UpdateDates

    suspend fun setUpdateDates(cache: Long, schedule: Long)
}