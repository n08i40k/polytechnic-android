package ru.n08i40k.polytechnic.next.network.request.schedule

import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ScheduleGetCacheStatus(
    appContainer: AppContainer,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : AuthorizedRequest(
    appContainer,
    Method.GET,
    "v1/schedule/cache-status",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val cacheUpdateRequired: Boolean,
        val cacheHash: String,
        val lastCacheUpdate: Long,
        val lastScheduleUpdate: Long,
    )
}