package ru.n08i40k.polytechnic.next.ui.screen.profile

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.android.volley.ClientError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.request.profile.ProfileChangeGroup
import ru.n08i40k.polytechnic.next.repository.profile.impl.MockProfileRepository
import ru.n08i40k.polytechnic.next.ui.helper.data.rememberInputValue
import ru.n08i40k.polytechnic.next.ui.widgets.selector.GroupSelector

private enum class ChangeGroupError {
    NOT_EXISTS
}

private fun tryChangeGroup(
    context: Context,
    group: String,
    onError: (ChangeGroupError) -> Unit,
    onSuccess: () -> Unit
) {
    ProfileChangeGroup(
        context.appContainer,
        ProfileChangeGroup.RequestDto(group),
        { onSuccess() },
        {
            if (it is ClientError && it.networkResponse.statusCode == 404)
                onError(ChangeGroupError.NOT_EXISTS)
            else throw it
        }
    ).send(context)
}

@Preview(showBackground = true)
@Composable
internal fun ChangeGroupDialog(
    context: Context = LocalContext.current,
    profile: Profile = MockProfileRepository.profile,
    onChange: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var group by rememberInputValue<String?>(profile.group) { it == null }

            var processing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                val modifier = Modifier.fillMaxWidth()

                GroupSelector(
                    group.value,
                    { group = group.copy(value = it, isError = false) },
                    isError = group.isError,
                    readOnly = processing,
                    supervised = profile.role == UserRole.TEACHER
                )

                val focusManager = LocalFocusManager.current
                Button(
                    {
                        processing = true
                        focusManager.clearFocus()

                        tryChangeGroup(
                            context = context,
                            group = group.value!!,
                            onError = {
                                group = group.copy(isError = true)

                                processing = false
                            },
                            onSuccess = onChange
                        )
                    },
                    modifier,
                    !(group.isError || processing) && group.value != null
                ) {
                    Text(stringResource(R.string.change_group))
                }
            }
        }
    }
}