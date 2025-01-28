package ru.n08i40k.polytechnic.next.network.request.schedule

import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.RequestBase

class ScheduleGetTeacherNames(
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener? = null
) : RequestBase(
    Method.GET,
    "v1/schedule/teacher-names",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class ResponseDto(
        val names: ArrayList<String>,
    )
}