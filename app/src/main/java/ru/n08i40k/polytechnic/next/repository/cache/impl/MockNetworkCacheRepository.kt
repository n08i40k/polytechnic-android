package ru.n08i40k.polytechnic.next.repository.cache.impl

import ru.n08i40k.polytechnic.next.CachedResponse
import ru.n08i40k.polytechnic.next.UpdateDates
import ru.n08i40k.polytechnic.next.repository.cache.NetworkCacheRepository

class MockNetworkCacheRepository : NetworkCacheRepository {
    override suspend fun get(url: String): CachedResponse? {
        return null
    }

    override suspend fun put(url: String, data: String) {}

    override suspend fun clear() {}

    override suspend fun isHashPresent(): Boolean {
        return true
    }

    override suspend fun setHash(hash: String) {}

    override suspend fun getUpdateDates(): UpdateDates {
        return UpdateDates.newBuilder().build()
    }

    override suspend fun setUpdateDates(cache: Long, schedule: Long) {}
}