package ru.n08i40k.polytechnic.next.ui.screen.auth.signup

import android.content.Context
import com.android.volley.ClientError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignUp
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignUp.Companion.ErrorCode
import java.util.concurrent.TimeoutException

enum class SignUpError {
    // server errors
    USERNAME_ALREADY_EXISTS,
    VK_ALREADY_EXISTS,
    INVALID_VK_ACCESS_TOKEN,
    INVALID_GROUP_NAME,
    DISALLOWED_ROLE,

    // client errors
    TIMED_OUT,
    NO_CONNECTION,

    UNKNOWN
}

fun mapError(exception: Throwable): SignUpError {
    return when (exception) {
        is TimeoutException  -> SignUpError.TIMED_OUT
        is TimeoutError      -> SignUpError.TIMED_OUT

        is NoConnectionError -> SignUpError.NO_CONNECTION

        is ClientError       -> {
            if (exception.networkResponse.statusCode != 406)
                return SignUpError.UNKNOWN

            val error = AuthSignUp.Companion.parseError(exception)

            when (error.code) {
                ErrorCode.USERNAME_ALREADY_EXISTS -> SignUpError.USERNAME_ALREADY_EXISTS
                ErrorCode.VK_ALREADY_EXISTS       -> SignUpError.VK_ALREADY_EXISTS
                ErrorCode.INVALID_VK_ACCESS_TOKEN -> SignUpError.INVALID_VK_ACCESS_TOKEN
                ErrorCode.INVALID_GROUP_NAME      -> SignUpError.INVALID_GROUP_NAME
                ErrorCode.DISALLOWED_ROLE         -> SignUpError.DISALLOWED_ROLE
            }
        }

        else                 -> SignUpError.UNKNOWN
    }
}

fun getErrorMessage(context: Context, error: SignUpError): String {
    return context.getString(
        when (error) {
            SignUpError.UNKNOWN                 -> R.string.unknown_error
            SignUpError.USERNAME_ALREADY_EXISTS -> R.string.auth_error_username_already_exists
            SignUpError.VK_ALREADY_EXISTS       -> R.string.auth_error_vk_already_exists
            SignUpError.INVALID_VK_ACCESS_TOKEN -> R.string.auth_error_invalid_vk_access_token
            SignUpError.INVALID_GROUP_NAME      -> R.string.auth_error_invalid_group_name
            SignUpError.DISALLOWED_ROLE         -> R.string.auth_error_disallowed_role
            SignUpError.TIMED_OUT               -> R.string.timed_out
            SignUpError.NO_CONNECTION           -> R.string.no_connection
        }
    )
}