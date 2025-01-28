package ru.n08i40k.polytechnic.next.ui.widgets.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.model.UserRole.Companion.AcceptableUserRoles


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RoleSelector(
    value: UserRole = UserRole.STUDENT,
    isError: Boolean = false,
    readOnly: Boolean = false,
    onValueChange: (UserRole) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !readOnly && !expanded }
        ) {
            TextField(
                label = { Text(stringResource(R.string.role)) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                value = stringResource(value.stringId),
                leadingIcon = {
                    Icon(
                        imageVector = value.icon,
                        contentDescription = "role icon"
                    )
                },
                onValueChange = {},
                isError = isError,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                AcceptableUserRoles.forEach {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = "role icon"
                            )
                        },
                        text = { Text(stringResource(it.stringId)) },
                        onClick = {
                            expanded = false
                            onValueChange(it)
                        }
                    )
                }
            }
        }
    }
}