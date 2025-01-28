package ru.n08i40k.polytechnic.next.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ru.n08i40k.polytechnic.next.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(badge: Boolean, items: List<@Composable ColumnScope.() -> Unit>) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                stringResource(R.string.app_name),
                Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        actions = {
            IconButton({ dropdownExpanded = true }) {
                BadgedBox({ if (badge) Badge() }) {
                    Icon(Icons.Filled.Menu, stringResource(R.string.cd_top_app_bar))
                }
            }

            DropdownMenu(dropdownExpanded, { dropdownExpanded = false }) {
                Column(Modifier.wrapContentWidth()) {
                    items.forEach { it() }
                }
            }
        }
    )
}