package ru.n08i40k.polytechnic.next.ui.screen.auth.signin

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignInVK
import ru.n08i40k.polytechnic.next.network.unwrapException
import ru.n08i40k.polytechnic.next.proto.settings
import ru.n08i40k.polytechnic.next.ui.widgets.OneTapComplete
import java.util.logging.Logger

private fun trySignIn(
    context: Context,

    accessToken: String,

    onSuccess: () -> Unit,
    onError: (SignInError) -> Unit,
) {
    AuthSignInVK(
        AuthSignInVK.RequestDto(accessToken),
        {
            runBlocking {
                context.settings.updateData { settings ->
                    settings
                        .toBuilder()
                        .setUserId(it.id)
                        .setAccessToken(it.accessToken)
                        .setGroup(it.group)
                        .build()
                }
            }

            onSuccess()
        },
        {
            val error = mapError(unwrapException(it))

            if (error == SignInError.UNKNOWN) {
                val logger = Logger.getLogger("trySignIn")

                logger.severe("Unknown exception while trying to sign-in!")
                logger.severe(it.toString())
            }

            onError(error)
        }
    ).send(context)
}


@Preview(showBackground = true)
@Composable
fun VKOneTap(
    toApp: () -> Unit = {},
    pushSnackbar: (String, SnackbarDuration) -> Unit = { _, _ -> },

    onProcess: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current

    OneTapComplete(
        onAuth = {
            onProcess(true)

            trySignIn(
                context,
                it,
                toApp
            ) {
                pushSnackbar(getErrorMessage(context, it, true), SnackbarDuration.Long)
                onProcess(false)
            }
        },
        onFail = {}
    )
}