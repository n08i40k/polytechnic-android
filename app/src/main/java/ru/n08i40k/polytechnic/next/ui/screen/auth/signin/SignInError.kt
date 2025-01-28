package ru.n08i40k.polytechnic.next.ui.screen.auth.signin

import android.content.Context
import com.android.volley.ClientError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignIn
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignIn.Companion.ErrorCode
import java.util.concurrent.TimeoutException

enum class SignInError {
    // server errors
    INCORRECT_CREDENTIALS,
    INVALID_VK_ACCESS_TOKEN,

    // client errors
    TIMED_OUT,
    NO_CONNECTION,

    UNKNOWN
}

fun mapError(exception: Throwable): SignInError {
    return when (exception) {
        is TimeoutException  -> SignInError.TIMED_OUT
        is TimeoutError      -> SignInError.TIMED_OUT

        is NoConnectionError -> SignInError.NO_CONNECTION

        is ClientError       -> {
            if (exception.networkResponse.statusCode != 406)
                return SignInError.UNKNOWN

            val error = AuthSignIn.Companion.parseError(exception)

            when (error.code) {
                ErrorCode.INVALID_VK_ACCESS_TOKEN -> SignInError.INVALID_VK_ACCESS_TOKEN
                ErrorCode.INCORRECT_CREDENTIALS   -> SignInError.INCORRECT_CREDENTIALS
            }
        }

        else                 -> SignInError.UNKNOWN
    }
}

fun getErrorMessage(context: Context, error: SignInError, isVK: Boolean): String {
    return context.getString(
        when (error) {
            SignInError.UNKNOWN                 -> R.string.unknown_error
            SignInError.INVALID_VK_ACCESS_TOKEN -> R.string.auth_error_invalid_vk_access_token
            SignInError.INCORRECT_CREDENTIALS   ->
                if (isVK)
                    R.string.auth_error_vk_not_linked
                else
                    R.string.auth_error_incorrect_credentials

            SignInError.TIMED_OUT               -> R.string.timed_out
            SignInError.NO_CONNECTION           -> R.string.no_connection
        }
    )
}