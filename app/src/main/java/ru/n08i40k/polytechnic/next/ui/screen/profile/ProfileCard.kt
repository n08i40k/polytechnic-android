package ru.n08i40k.polytechnic.next.ui.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.repository.profile.impl.MockProfileRepository

private enum class ChangeValue {
    NONE,
    USERNAME,
    PASSWORD,
    GROUP
}

@Preview(showSystemUi = true)
@Composable
fun ProfileCard(profile: Profile = MockProfileRepository.profile, refresh: () -> Unit = {}) {
    Box(Modifier.padding(20.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            var columnSize by remember { mutableStateOf(10.dp) }
            val localDensity = LocalDensity.current

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(10.dp)
                    .onGloballyPositioned {
                        with(localDensity) {
                            columnSize = it.size.width.toDp()
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                val focusManager = LocalFocusManager.current

                var change by remember { mutableStateOf<ChangeValue>(ChangeValue.NONE) }

                TextField(
                    profile.username,
                    {},
                    Modifier.onFocusChanged {
                        if (it.isFocused && profile.role !== UserRole.TEACHER) {
                            change = ChangeValue.USERNAME
                            focusManager.clearFocus(true)
                        }
                    },
                    readOnly = true,
                    label = { Text(stringResource(R.string.username)) },
                    leadingIcon = {
                        Icon(Icons.Filled.AccountCircle, stringResource(R.string.cd_profile_icon))
                    }
                )

                TextField(
                    "12345678",
                    {},
                    Modifier.onFocusChanged {
                        if (it.isFocused) {
                            change = ChangeValue.PASSWORD
                            focusManager.clearFocus(true)
                        }
                    },
                    readOnly = true,
                    label = { Text(stringResource(R.string.password)) },
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, stringResource(R.string.cd_password_icon))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                )

                TextField(
                    stringResource(profile.role.stringId),
                    {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.role)) },
                    leadingIcon = {
                        Icon(profile.role.icon, stringResource(R.string.cd_role_icon))
                    },
                )

                TextField(
                    profile.group,
                    {},
                    Modifier.onFocusChanged {
                        if (it.isFocused) {
                            change = ChangeValue.GROUP
                            focusManager.clearFocus()
                        }
                    },
                    true,
                    label = { Text(stringResource(R.string.group)) },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, stringResource(R.string.cd_group_icon))
                    },
                )

                Button({
                    val repo = context.applicationContext.appContainer.profileRepository
                    runBlocking { repo.signOut() }
                }, Modifier.width(columnSize)) {
                    Text(stringResource(R.string.sign_out))
                }

                val onDismiss: () -> Unit = {
                    change = ChangeValue.NONE
                }

                val onChange: () -> Unit = {
                    change = ChangeValue.NONE
                    refresh()
                }

                when (change) {
                    ChangeValue.NONE -> Unit
                    ChangeValue.USERNAME -> ChangeUsernameDialog(context, onChange, onDismiss)
                    ChangeValue.PASSWORD -> ChangePasswordDialog(context, onChange, onDismiss)
                    ChangeValue.GROUP -> ChangeGroupDialog(context, profile, onChange, onDismiss)
                }
            }
        }
    }
}