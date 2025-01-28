package ru.n08i40k.polytechnic.next.ui.screen.profile

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.android.volley.ClientError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.network.request.profile.ProfileChangeUsername
import ru.n08i40k.polytechnic.next.ui.helper.data.rememberInputValue

private enum class ChangeUsernameError {
    INCORRECT_LENGTH,
    ALREADY_EXISTS
}

private fun tryChangeUsername(
    context: Context,
    username: String,
    onError: (ChangeUsernameError) -> Unit,
    onSuccess: () -> Unit
) {
    ProfileChangeUsername(
        context.appContainer,
        ProfileChangeUsername.RequestDto(username),
        {
            onSuccess()
        },
        {
            if (it is ClientError && it.networkResponse.statusCode == 409)
                onError(ChangeUsernameError.ALREADY_EXISTS)
            if (it is ClientError && it.networkResponse.statusCode == 400)
                onError(ChangeUsernameError.INCORRECT_LENGTH)
            else throw it
        }
    ).send(context)
}

@Preview(showBackground = true)
@Composable
internal fun ChangeUsernameDialog(
    context: Context = LocalContext.current,
    onChange: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var username by rememberInputValue("") { it.length < 4 }

            var processing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                val modifier = Modifier.fillMaxWidth()

                OutlinedTextField(
                    username.value,
                    { username = username.copy(value = it.filter { it != ' ' }.lowercase()) },
                    modifier,
                    readOnly = processing,
                    label = { Text(text = stringResource(R.string.username)) },
                    isError = username.isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        KeyboardType.Ascii,
                        ImeAction.Next
                    )
                )

                val focusManager = LocalFocusManager.current
                Button(
                    modifier = modifier,
                    onClick = {
                        processing = true
                        focusManager.clearFocus()

                        tryChangeUsername(
                            context = context,
                            username = username.value,
                            onError = {
                                username = username.copy(isError = true)
                                processing = false
                            },
                            onSuccess = onChange
                        )
                    },
                    enabled = !(username.isError || processing)
                ) {
                    Text(stringResource(R.string.change_username))
                }
            }
        }
    }
}