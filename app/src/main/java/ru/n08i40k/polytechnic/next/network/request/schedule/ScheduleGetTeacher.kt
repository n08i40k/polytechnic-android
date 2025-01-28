package ru.n08i40k.polytechnic.next.network.request.schedule

import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.network.request.CachedRequest

class ScheduleGetTeacher(
    appContainer: AppContainer,
    teacher: String,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : CachedRequest(
    appContainer,
    Method.GET,
    "v1/schedule/teacher/$teacher",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val updatedAt: String,
        val teacher: GroupOrTeacher,
        val updated: ArrayList<Int>,
    )
}