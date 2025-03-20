package ru.n08i40k.polytechnic.next.ui.screen.auth.signin

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignIn
import ru.n08i40k.polytechnic.next.network.unwrapException
import ru.n08i40k.polytechnic.next.proto.settings
import ru.n08i40k.polytechnic.next.ui.helper.PushSnackbar
import ru.n08i40k.polytechnic.next.ui.helper.data.rememberInputValue
import java.util.logging.Logger

private fun trySignIn(
    context: Context,

    username: String,
    password: String,

    onSuccess: () -> Unit,
    onError: (SignInError) -> Unit,
) {
    AuthSignIn(
        AuthSignIn.RequestDto(username, password),
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
                val logger = Logger.getLogger("tryRegister")

                logger.severe("Unknown exception while trying to register!")
                logger.severe(it.toString())
            }

            onError(error)
        }
    ).send(context)
}


@Composable
internal fun ManualPage(
    pushSnackbar: PushSnackbar,
    toApp: () -> Unit,
    toSelect: () -> Unit,
    parentWidth: Dp,
) {
    val context = LocalContext.current

    var username by rememberInputValue<String>("") { it.length < 4 }
    var password by rememberInputValue<String>("") { it.isEmpty() }

    var invalidCredentials by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val onClick: () -> Unit = fun() {
        focusManager.clearFocus(true)

        loading = true

        trySignIn(
            context,
            username.value,
            password.value,
            {
                loading = false
                toApp()
            },
            {
                loading = false

                when (it) {
                    SignInError.INCORRECT_CREDENTIALS -> username = username.copy(isError = true)
                    else                              -> Unit
                }

                pushSnackbar(getErrorMessage(context, it, false), SnackbarDuration.Long)
            }
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.defaultMinSize(parentWidth, Dp.Unspecified),
            contentAlignment = Alignment.Center
        ) {
            if (parentWidth != Dp.Unspecified) {
                Row(Modifier.width(parentWidth)) {
                    IconButton(toSelect) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            stringResource(R.string.cd_back_icon)
                        )
                    }
                }
            }

            Text(
                stringResource(R.string.sign_in_title),
                Modifier.padding(10.dp),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        val onValueChange: () -> Unit = {
            if (invalidCredentials) {
                invalidCredentials = false

                username = username.copy(isError = false, checkNow = true)
                password = password.copy(isError = false, checkNow = true)
            }
        }

        OutlinedTextField(
            username.value,
            {
                onValueChange()

                username = username.copy(
                    it.filter { it != ' ' }.lowercase(),
                    isError = false
                )
            },
            readOnly = loading,
            label = { Text(stringResource(R.string.username)) },
            isError = username.isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                KeyboardType.Ascii,
                ImeAction.Next
            )
        )

        OutlinedTextField(
            password.value,
            {
                onValueChange()

                password = password.copy(
                    it,
                    isError = false
                )
            },
            readOnly = loading,
            label = { Text(stringResource(R.string.password)) },
            isError = password.isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                KeyboardType.Password,
                ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        if (parentWidth != Dp.Unspecified) {
            Spacer(Modifier.height(10.dp))

            val canProceed = !loading
                    && (!username.isError && username.value.isNotEmpty())
                    && (!password.isError && password.value.isNotEmpty())
            Button(
                onClick,
                Modifier.width(parentWidth),
                enabled = canProceed
            ) {
                Text(
                    stringResource(R.string.proceed),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}