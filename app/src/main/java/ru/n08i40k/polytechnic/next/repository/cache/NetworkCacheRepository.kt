package ru.n08i40k.polytechnic.next.repository.cache

import ru.n08i40k.polytechnic.next.CacheDate
import ru.n08i40k.polytechnic.next.CacheResponse

interface NetworkCacheRepository {
    suspend fun put(url: String, data: String)

    suspend fun get(url: String): CacheResponse?

    suspend fun clear()

    suspend fun isHashPresent(): Boolean

    suspend fun setHash(hash: String)

    suspend fun getUpdateDates(): CacheDate

    suspend fun setUpdateDates(cache: Long, schedule: Long)
}