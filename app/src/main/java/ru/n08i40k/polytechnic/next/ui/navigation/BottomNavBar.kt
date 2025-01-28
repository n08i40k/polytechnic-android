package ru.n08i40k.polytechnic.next.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    @StringRes val label: Int,
    val icon: ImageVector,
    val route: String,
)

@Composable
fun BottomNavBar(navHostController: NavHostController, items: List<BottomNavItem>) {
    NavigationBar {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()

        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach {
            NavigationBarItem(
                selected = it.route == currentRoute,
                onClick = { if (it.route != currentRoute) navHostController.navigate(it.route) },
                icon = { Icon(it.icon, stringResource(it.label)) },
                label = { Text(stringResource(it.label)) }
            )
        }
    }
}