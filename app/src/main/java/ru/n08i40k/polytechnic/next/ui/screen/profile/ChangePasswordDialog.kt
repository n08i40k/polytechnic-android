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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.android.volley.AuthFailureError
import com.android.volley.ClientError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.network.request.auth.AuthChangePassword
import ru.n08i40k.polytechnic.next.ui.helper.data.rememberInputValue

private enum class ChangePasswordError {
    INCORRECT_CURRENT_PASSWORD,
    SAME_PASSWORDS
}

private fun tryChangePassword(
    context: Context,
    oldPassword: String,
    newPassword: String,
    onSuccess: () -> Unit,
    onError: (ChangePasswordError) -> Unit
) {
    AuthChangePassword(
        context.appContainer,
        AuthChangePassword.RequestDto(oldPassword, newPassword),
        { onSuccess() },
        {
            if (it is ClientError && it.networkResponse.statusCode == 409)
                onError(ChangePasswordError.SAME_PASSWORDS)
            else if (it is AuthFailureError)
                onError(ChangePasswordError.INCORRECT_CURRENT_PASSWORD)
            else throw it
        }
    ).send(context)
}

@Preview(showBackground = true)
@Composable
internal fun ChangePasswordDialog(
    context: Context = LocalContext.current,
    onChange: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var oldPassword by rememberInputValue<String>("") { it.isEmpty() }
            var newPassword by rememberInputValue<String>("") { it.isEmpty() || it == oldPassword.value }

            var processing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                val modifier = Modifier.fillMaxWidth()

                OutlinedTextField(
                    oldPassword.value,
                    {
                        oldPassword = oldPassword.copy(value = it, isError = false)
                        newPassword = newPassword.copy(isError = false, checkNow = true)
                    },
                    modifier,
                    readOnly = processing,
                    label = { Text(text = stringResource(R.string.old_password)) },
                    isError = oldPassword.isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        KeyboardType.Password,
                        ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )

                OutlinedTextField(
                    newPassword.value,
                    { newPassword = newPassword.copy(value = it, isError = false) },
                    modifier,
                    readOnly = processing,
                    label = { Text(text = stringResource(R.string.new_password)) },
                    isError = newPassword.isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        KeyboardType.Password,
                        ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )

                val focusManager = LocalFocusManager.current
                Button(
                    {
                        processing = true
                        focusManager.clearFocus()

                        tryChangePassword(
                            context,
                            oldPassword.value,
                            newPassword.value,
                            onChange
                        ) {
                            when (it) {
                                ChangePasswordError.SAME_PASSWORDS -> {
                                    oldPassword.copy(isError = true)
                                    newPassword.copy(isError = true)
                                }

                                ChangePasswordError.INCORRECT_CURRENT_PASSWORD -> {
                                    oldPassword.isError = true
                                }
                            }

                            processing = false
                        }
                    },
                    modifier,
                    !(newPassword.isError || oldPassword.isError || processing)
                ) {
                    Text(stringResource(R.string.change_password))
                }
            }
        }
    }
}