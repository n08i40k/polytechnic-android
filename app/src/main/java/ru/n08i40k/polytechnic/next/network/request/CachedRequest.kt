package ru.n08i40k.polytechnic.next.network.request

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.Application
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.network.NetworkConnection
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGetCacheStatus
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleUpdate
import ru.n08i40k.polytechnic.next.network.tryFuture
import ru.n08i40k.polytechnic.next.network.tryGet
import ru.n08i40k.polytechnic.next.utils.MyResult
import java.util.logging.Logger
import java.util.regex.Pattern

open class CachedRequest(
    appContainer: AppContainer,
    method: Int,
    private val url: String,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?,
) : AuthorizedRequest(
    appContainer,
    method,
    url,
    {
        runBlocking(Dispatchers.IO) {
            appContainer.networkCacheRepository.put(url, it)
        }
        listener.onResponse(it)
    },
    errorListener
) {

    private suspend fun getXlsUrl(): MyResult<String> = withContext(Dispatchers.IO) {
        val mainPageFuture = RequestFuture.newFuture<String>()
        val request = StringRequest(
            Method.GET,
            "https://politehnikum-eng.ru/index/raspisanie_zanjatij/0-409",
            mainPageFuture,
            mainPageFuture
        )
        NetworkConnection.getInstance(appContext).addToRequestQueue(request)

        val response = tryGet(mainPageFuture)
        if (response is MyResult.Failure)
            return@withContext response

        val pageData = (response as MyResult.Success).data

        val remoteConfig =
            (appContext.applicationContext as Application).container.remoteConfig

        val pattern: Pattern =
            Pattern.compile(remoteConfig.getString("linkParserRegex"), Pattern.MULTILINE)

        val matcher = pattern.matcher(pageData)
        if (!matcher.find())
            return@withContext MyResult.Failure(RuntimeException("Required url not found!"))

        MyResult.Success("https://politehnikum-eng.ru" + matcher.group(1))
    }


    private suspend fun updateMainPage(): MyResult<ScheduleGetCacheStatus.ResponseDto> {
        return withContext(Dispatchers.IO) {
            when (val xlsUrl = getXlsUrl()) {
                is MyResult.Failure -> xlsUrl
                is MyResult.Success -> {
                    tryFuture(appContext) { it ->
                        ScheduleUpdate(
                            appContext.appContainer,
                            ScheduleUpdate.RequestDto(xlsUrl.data),
                            it,
                            it
                        )
                    }
                }
            }
        }
    }

    override fun send(context: Context) {
        // TODO: network cache
        val logger = Logger.getLogger("CachedRequest")
        val cache = appContainer.networkCacheRepository

        val cacheStatusResult = tryFuture(context) {
            ScheduleGetCacheStatus(appContainer, it, it)
        }

        if (cacheStatusResult is MyResult.Success) {
            val cacheStatus = cacheStatusResult.data

            runBlocking {
                cache.setUpdateDates(
                    cacheStatus.lastCacheUpdate,
                    cacheStatus.lastScheduleUpdate
                )
                cache.setHash(cacheStatus.cacheHash)
            }

            if (cacheStatus.cacheUpdateRequired) {
                val updateResult = runBlocking { updateMainPage() }

                when (updateResult) {
                    is MyResult.Success -> {
                        runBlocking {
                            cache.setUpdateDates(
                                updateResult.data.lastCacheUpdate,
                                updateResult.data.lastScheduleUpdate
                            )
                            cache.setHash(updateResult.data.cacheHash)
                        }
                    }

                    is MyResult.Failure -> {
                        logger.warning("Failed to update cache!")
                    }
                }
            }
        } else {
            logger.warning("Failed to get cache status!")
        }

        val cachedResponse = runBlocking { cache.get(url) }
        if (cachedResponse != null) {
            listener.onResponse(cachedResponse.data)
            return
        }

        super.send(context)
    }
}