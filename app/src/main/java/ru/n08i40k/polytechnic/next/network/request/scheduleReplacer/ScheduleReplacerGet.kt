package ru.n08i40k.polytechnic.next.network.request.scheduleReplacer

import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.ScheduleReplacer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

// TODO: вернуть
@Suppress("unused")
class ScheduleReplacerGet(
    appContainer: AppContainer,
    listener: Response.Listener<List<ScheduleReplacer>>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    appContainer,
    Method.GET,
    "v1/schedule-replacer/get",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
)