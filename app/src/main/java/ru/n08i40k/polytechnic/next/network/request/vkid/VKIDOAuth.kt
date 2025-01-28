package ru.n08i40k.polytechnic.next.network.request.vkid

import com.android.volley.Request.Method
import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.network.RequestBase

class VKIDOAuth(
    private val data: RequestDto,
    listener: Response.Listener<ResponseDto>,
    errorListener: Response.ErrorListener?,
) : RequestBase(
    Method.POST,
    "v1/vkid/oauth",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class RequestDto(
        val code: String,
        val codeVerifier: String,
        val deviceId: String,
    )

    @Serializable
    data class ResponseDto(
        val accessToken: String,
    )

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}