package ru.n08i40k.polytechnic.next.network.request.auth

import com.android.volley.Response
import com.android.volley.VolleyError
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.RequestBase
import ru.n08i40k.polytechnic.next.utils.EnumAsStringSerializer

class AuthSignUp(
    private val data: RequestDto,
    listener: Response.Listener<Profile>,
    errorListener: Response.ErrorListener?
) : RequestBase(
    Method.POST,
    "v1/auth/sign-up",
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
            USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS"),
            VK_ALREADY_EXISTS("VK_ALREADY_EXISTS"),
            INVALID_VK_ACCESS_TOKEN("INVALID_VK_ACCESS_TOKEN"),
            INVALID_GROUP_NAME("INVALID_GROUP_NAME"),
            DISALLOWED_ROLE("DISALLOWED_ROLE"),
        }

        @Serializable
        data class Error(val code: ErrorCode)

        fun parseError(error: VolleyError): Error {
            return Json.decodeFromString<Error>(error.networkResponse.data.decodeToString())
        }
    }

    @Serializable
    data class RequestDto(
        val username: String,
        val password: String,
        val group: String,
        val role: UserRole,
        val version: String
    )

    override fun getBody(): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}