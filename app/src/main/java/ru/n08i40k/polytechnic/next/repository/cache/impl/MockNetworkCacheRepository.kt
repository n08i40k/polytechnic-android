package ru.n08i40k.polytechnic.next.repository.cache.impl

import ru.n08i40k.polytechnic.next.CacheDate
import ru.n08i40k.polytechnic.next.CacheResponse
import ru.n08i40k.polytechnic.next.repository.cache.NetworkCacheRepository

class MockNetworkCacheRepository : NetworkCacheRepository {
    override suspend fun get(url: String): CacheResponse? {
        return null
    }

    override suspend fun put(url: String, data: String) {}

    override suspend fun clear() {}

    override suspend fun isHashPresent(): Boolean {
        return true
    }

    override suspend fun setHash(hash: String) {}

    override suspend fun getUpdateDates(): CacheDate {
        return CacheDate.newBuilder().build()
    }

    override suspend fun setUpdateDates(cache: Long, schedule: Long) {}
}