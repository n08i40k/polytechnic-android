package ru.n08i40k.polytechnic.next.ui.widgets.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.n08i40k.polytechnic.next.R

@Composable
private fun DropdownMenuItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem({ Text(text) }, onClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSelector(
    value: String? = null,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    title: String,
    leadingIcon: @Composable () -> Unit,
    isError: Boolean = false,
    loader: @Composable ((String?) -> Unit) -> ArrayList<String>?,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        val variants = loader { it?.apply { onValueChange(it) } }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !readOnly && !expanded && variants?.isNotEmpty() == true
            }
        ) {
            val context = LocalContext.current

            val viewValue =
                if (variants == null) stringResource(R.string.loading)
                else value ?: variants.getOrElse(0) { context.getString(R.string.failed_to_fetch) }

            TextField(
                viewValue,
                {},
                modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                readOnly = true,
                label = { Text(title) },
                leadingIcon = leadingIcon,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = isError
            )

            ExposedDropdownMenu(expanded, { expanded = false }) {
                variants?.forEach {
                    DropdownMenuItem(it) {
                        if (variants.isNotEmpty())
                            onValueChange(it)

                        expanded = false
                    }
                }
            }
        }
    }
}