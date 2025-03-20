package ru.n08i40k.polytechnic.next.repository.cache.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.CacheDate
import ru.n08i40k.polytechnic.next.CacheResponse
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.proto.cache
import ru.n08i40k.polytechnic.next.repository.cache.NetworkCacheRepository
import javax.inject.Inject

class LocalNetworkCacheRepository
@Inject constructor(private val appContainer: AppContainer) : NetworkCacheRepository {
    private val cacheMap: MutableMap<String, CacheResponse> = mutableMapOf()
    private var cacheDate: CacheDate = CacheDate.newBuilder().build()

    private var hash: String? = null

    private val context get() = appContainer.context

    init {
        cacheMap.clear()

        runBlocking {
            cacheMap.putAll(
                context
                    .cache
                    .data
                    .map { it.storageMap }.first()
            )
        }
    }

    override suspend fun get(url: String): CacheResponse? {
        // Если кешированного ответа нет, то возвращаем null
        // Если хеши не совпадают и локальный хеш присутствует, то возвращаем null

        val response = cacheMap[url] ?: return null

        if (response.hash != this.hash && this.hash != null)
            return null

        return response
    }

    override suspend fun put(url: String, data: String) {
        if (hash == null)
            throw IllegalStateException("Не установлен хеш!")

        cacheMap[url] = CacheResponse
            .newBuilder()
            .setHash(this.hash)
            .setData(data)
            .build()

        save()
    }

    override suspend fun clear() {
        this.cacheMap.clear()
        this.save()
    }

    override suspend fun isHashPresent(): Boolean {
        return this.hash != null
    }

    override suspend fun setHash(hash: String) {
        val freshHash = this.hash == null

        if (!freshHash && this.hash != hash)
            clear()

        this.hash = hash

        if (freshHash) {
            this.cacheMap
                .mapNotNull { if (it.value.hash != this.hash) it.key else null }
                .forEach { this.cacheMap.remove(it) }
            save()
        }
    }

    override suspend fun getUpdateDates(): CacheDate {
        return this.cacheDate
    }

    override suspend fun setUpdateDates(cache: Long, schedule: Long) {
        cacheDate = CacheDate
            .newBuilder()
            .setCache(cache)
            .setSchedule(schedule).build()

        withContext(Dispatchers.IO) {
            context.cache.updateData {
                it
                    .toBuilder()
                    .setDate(cacheDate)
                    .build()
            }
        }
        save()
    }

    private suspend fun save() {
        withContext(Dispatchers.IO) {
            context.cache.updateData {
                it
                    .toBuilder()
                    .putAllStorage(cacheMap)
                    .build()
            }
        }
    }
}