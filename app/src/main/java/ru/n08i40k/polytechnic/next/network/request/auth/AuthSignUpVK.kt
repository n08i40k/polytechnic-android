package ru.n08i40k.polytechnic.next.network.request.auth

import com.android.volley.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.RequestBase

class AuthSignUpVK(
    private val data: RequestDto,
    listener: Response.Listener<Profile>,
    errorListener: Response.ErrorListener?
) : RequestBase(
    Method.POST,
    "v1/auth/sign-up-vk",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    @Serializable
    data class RequestDto(
        val accessToken: String,
        val username: String,
        val group: String,
        val role: UserRole,
        val version: String
    )

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}