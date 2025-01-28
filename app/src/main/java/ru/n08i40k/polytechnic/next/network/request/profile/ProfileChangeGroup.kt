package ru.n08i40k.polytechnic.next.network.request.profile

import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ProfileChangeGroup(
    appContainer: AppContainer,
    private val data: RequestDto,
    listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    appContainer,
    Method.POST,
    "v1/users/change-group",
    { listener.onResponse(null) },
    errorListener
) {
    @Serializable
    data class RequestDto(val group: String)

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}