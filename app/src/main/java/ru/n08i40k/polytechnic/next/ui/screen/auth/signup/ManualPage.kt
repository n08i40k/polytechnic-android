package ru.n08i40k.polytechnic.next.ui.screen.auth.signup

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
import ru.n08i40k.polytechnic.next.Application
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignUp
import ru.n08i40k.polytechnic.next.network.unwrapException
import ru.n08i40k.polytechnic.next.settings.settings
import ru.n08i40k.polytechnic.next.ui.helper.PushSnackbar
import ru.n08i40k.polytechnic.next.ui.helper.data.rememberInputValue
import ru.n08i40k.polytechnic.next.ui.widgets.selector.GroupSelector
import ru.n08i40k.polytechnic.next.ui.widgets.selector.RoleSelector
import ru.n08i40k.polytechnic.next.ui.widgets.selector.TeacherNameSelector
import java.util.logging.Logger

private fun trySignUp(
    context: Context,

    username: String,
    password: String,
    group: String,
    role: UserRole,

    onSuccess: () -> Unit,
    onError: (SignUpError) -> Unit,
) {
    AuthSignUp(
        AuthSignUp.RequestDto(
            username,
            password,
            group,
            role,
            (context.applicationContext as Application).version
        ),
        {
            runBlocking {
                context.settings.updateData { settings ->
                    settings
                        .toBuilder()
                        .setUserId(it.id)
                        .setAccessToken(it.accessToken)
                        .setGroup(group)
                        .build()
                }
            }

            onSuccess()
        },
        {
            val error = mapError(unwrapException(it))

            if (error == SignUpError.UNKNOWN) {
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
    var group by rememberInputValue<String?>(null) { it == null }
    var role by remember { mutableStateOf(UserRole.STUDENT) }

    var loading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val onClick: () -> Unit = fun() {
        focusManager.clearFocus(true)

        loading = true

        trySignUp(
            context,
            username.value,
            password.value,
            group.value!!,
            role,
            {
                loading = false
                toApp()
            },
            {
                loading = false

                when (it) {
                    SignUpError.USERNAME_ALREADY_EXISTS -> username = username.copy(isError = true)
                    SignUpError.INVALID_GROUP_NAME      -> group = group.copy(isError = true)
                    else                                -> Unit
                }

                pushSnackbar(getErrorMessage(context, it), SnackbarDuration.Long)
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
                stringResource(R.string.sign_up_title),
                Modifier.padding(10.dp),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        when (role) {
            UserRole.TEACHER -> {
                TeacherNameSelector(
                    username.value, {
                        username = username.copy(
                            value = it,
                            isError = false
                        )
                    },
                    isError = username.isError,
                    readOnly = loading
                )
            }

            UserRole.STUDENT -> {
                OutlinedTextField(
                    username.value,
                    {
                        username = username.copy(
                            value = it.filter { it != ' ' }.lowercase(),
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
            }

            else             -> Unit
        }

        OutlinedTextField(
            password.value,
            {
                password = password.copy(
                    value = it,
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
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(Modifier.height(10.dp))

        GroupSelector(
            group.value,
            {
                group = group.copy(
                    value = it,
                    isError = false
                )
            },
            isError = group.isError,
            readOnly = loading,
            supervised = role == UserRole.TEACHER
        )

        Spacer(Modifier.height(10.dp))

        RoleSelector(
            role,
            false,
            loading
        ) { role = it }

        if (parentWidth != Dp.Unspecified) {
            Spacer(Modifier.height(10.dp))

            val canProceed = !loading
                    && (!username.isError && username.value.isNotEmpty())
                    && (!password.isError && password.value.isNotEmpty())
                    && (!group.isError && group.value != null)

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