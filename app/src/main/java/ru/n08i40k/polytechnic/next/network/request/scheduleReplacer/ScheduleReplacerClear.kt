package ru.n08i40k.polytechnic.next.network.request.scheduleReplacer

import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

// TODO: вернуть
@Suppress("unused")
class ScheduleReplacerClear(
    appContainer: AppContainer,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    appContainer,
    Method.POST,
    "v1/schedule-replacer/clear",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val count: Int
    )
}