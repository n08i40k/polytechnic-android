package ru.n08i40k.polytechnic.next.network.request.auth

import com.android.volley.Response
import com.android.volley.VolleyError
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.RequestBase
import ru.n08i40k.polytechnic.next.utils.EnumAsStringSerializer

class AuthSignIn(
    private val data: RequestDto,
    listener: Response.Listener<Profile>,
    errorListener: Response.ErrorListener?
) : RequestBase(
    Method.POST,
    "v1/auth/sign-in",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
) {
    companion object {
        private class ErrorCodeSerializer : EnumAsStringSerializer<ErrorCode>(
            "SignInErrorCode",
            { it.value },
            { v -> ErrorCode.entries.first { it.value == v } }
        )

        @Serializable(with = ErrorCodeSerializer::class)
        enum class ErrorCode(val value: String) {
            INCORRECT_CREDENTIALS("INCORRECT_CREDENTIALS"),
            INVALID_VK_ACCESS_TOKEN("INVALID_VK_ACCESS_TOKEN"),
        }

        @Serializable
        data class Error(val code: ErrorCode)

        fun parseError(error: VolleyError): Error {
            return Json.decodeFromString<Error>(error.networkResponse.data.decodeToString())
        }
    }

    @Serializable
    data class RequestDto(val username: String, val password: String)

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}