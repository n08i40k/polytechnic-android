package ru.n08i40k.polytechnic.next.ui.screen

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.Application
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.settings.settings
import ru.n08i40k.polytechnic.next.ui.AppRoute
import ru.n08i40k.polytechnic.next.ui.icons.AppIcons
import ru.n08i40k.polytechnic.next.ui.icons.appicons.Filled
import ru.n08i40k.polytechnic.next.ui.icons.appicons.filled.Download
import ru.n08i40k.polytechnic.next.ui.icons.appicons.filled.Telegram
import ru.n08i40k.polytechnic.next.ui.model.GroupViewModel
import ru.n08i40k.polytechnic.next.ui.model.ProfileUiState
import ru.n08i40k.polytechnic.next.ui.model.ProfileViewModel
import ru.n08i40k.polytechnic.next.ui.model.RemoteConfigUiState
import ru.n08i40k.polytechnic.next.ui.model.RemoteConfigViewModel
import ru.n08i40k.polytechnic.next.ui.model.SearchViewModel
import ru.n08i40k.polytechnic.next.ui.model.TeacherViewModel
import ru.n08i40k.polytechnic.next.ui.navigation.BottomNavBar
import ru.n08i40k.polytechnic.next.ui.navigation.BottomNavItem
import ru.n08i40k.polytechnic.next.ui.navigation.NavHostContainer
import ru.n08i40k.polytechnic.next.ui.navigation.TopAppBar
import ru.n08i40k.polytechnic.next.ui.screen.profile.ProfileScreen
import ru.n08i40k.polytechnic.next.ui.screen.replacer.ReplacerScreen
import ru.n08i40k.polytechnic.next.ui.screen.schedule.GroupScheduleScreen
import ru.n08i40k.polytechnic.next.ui.screen.schedule.TeacherScheduleScreen
import ru.n08i40k.polytechnic.next.ui.screen.schedule.TeacherSearchScreen
import ru.n08i40k.polytechnic.next.ui.widgets.LoadingContent
import ru.n08i40k.polytechnic.next.utils.openLink

private data class MainBottomNavItem(
    val bottomNavItem: BottomNavItem,
    val requiredRole: UserRole?
)

private enum class MainScreenRoute(val route: String) {
    PROFILE("profile"),
    REPLACER("replacer"),
    TEACHER_SCHEDULE("teacher-schedule"),
    GROUP_SCHEDULE("group-schedule"),
    TEACHER_SEARCH("teacher-search")
}

private val mainNavBarItems = listOf(
    MainBottomNavItem(
        BottomNavItem(
            R.string.profile,
            Icons.Filled.AccountCircle,
            MainScreenRoute.PROFILE.route
        ),
        null
    ),
    MainBottomNavItem(
        BottomNavItem(
            R.string.replacer,
            Icons.Filled.Create,
            MainScreenRoute.REPLACER.route
        ),
        UserRole.ADMIN
    ),
    MainBottomNavItem(
        BottomNavItem(
            R.string.teacher_schedule,
            Icons.Filled.Person,
            MainScreenRoute.TEACHER_SCHEDULE.route
        ),
        UserRole.TEACHER
    ),
    MainBottomNavItem(
        BottomNavItem(
            R.string.group_schedule,
            Icons.Filled.DateRange,
            MainScreenRoute.GROUP_SCHEDULE.route
        ),
        null
    ),
    MainBottomNavItem(
        BottomNavItem(
            R.string.teachers_schedule,
            Icons.Filled.Person,
            MainScreenRoute.TEACHER_SEARCH.route
        ),
        UserRole.STUDENT
    ),
)

@Composable
private fun LinkButton(
    text: String,
    icon: ImageVector,
    link: String,
    enabled: Boolean = true,
    badged: Boolean = false,
) {
    val context = LocalContext.current

    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { context.openLink(link) },
        enabled = enabled,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgedBox(badge = { if (badged) Badge() }) {
                Icon(
                    imageVector = icon,
                    contentDescription = text
                )
            }
            Spacer(Modifier.width(5.dp))
            Text(text)
        }
    }
}

private fun topBarItems(
    context: Context,
    remoteConfigUiState: RemoteConfigUiState
): Pair<Boolean, List<@Composable ColumnScope.() -> Unit>> {
    val packageVersion = (context.applicationContext as Application).version
    val updateAvailable = remoteConfigUiState.currVersion != packageVersion

    return Pair<Boolean, List<@Composable ColumnScope.() -> Unit>>(
        updateAvailable,
        listOf(
            {
                LinkButton(
                    text = stringResource(R.string.download_update),
                    icon = AppIcons.Filled.Download,
                    link = remoteConfigUiState.downloadLink,
                    enabled = updateAvailable,
                    badged = updateAvailable
                )
            },
            {
                LinkButton(
                    text = stringResource(R.string.telegram_channel),
                    icon = AppIcons.Filled.Telegram,
                    link = remoteConfigUiState.telegramLink,
                )
            }
        )
    )
}

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(context) {
        runBlocking {
            val accessToken = context.settings.data.map { it.accessToken }.first()

            if (accessToken.isEmpty()) {
                navController.navigate(AppRoute.AUTH.route) {
                    popUpTo(AppRoute.AUTH.route) { inclusive = true }
                }
            }
        }
    }

    val viewModelStoreOwner = LocalActivity.current as ComponentActivity

    val profileViewModel = hiltViewModel<ProfileViewModel>(viewModelStoreOwner)
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    LoadingContent(
        empty = false,
        emptyContent = {},
        loading = profileUiState.isLoading,
    ) {
        val profile =
            if (profileUiState is ProfileUiState.HasData)
                (profileUiState as ProfileUiState.HasData).profile
            else
                null

        val role = profile?.role ?: UserRole.STUDENT

        val items =
            mainNavBarItems.filter {
                it.requiredRole == null
                        || (role == UserRole.ADMIN
                        || it.requiredRole == role)
            }

        val groupViewModel = hiltViewModel<GroupViewModel>(viewModelStoreOwner)

        val remoteConfigViewModel = hiltViewModel<RemoteConfigViewModel>(viewModelStoreOwner)
        val remoteConfigUiState by remoteConfigViewModel.uiState.collectAsStateWithLifecycle()

        val teacherViewModel =
            if (role === UserRole.STUDENT)
                null
            else
                hiltViewModel<TeacherViewModel>(viewModelStoreOwner)

        val searchViewModel =
            if (role === UserRole.TEACHER)
                null
            else
                hiltViewModel<SearchViewModel>(viewModelStoreOwner)

        val routes = mapOf<String, @Composable () -> Unit>(
            MainScreenRoute.PROFILE.route to { ProfileScreen(profileViewModel) },
            MainScreenRoute.REPLACER.route to { ReplacerScreen() },
            MainScreenRoute.TEACHER_SCHEDULE.route to { TeacherScheduleScreen(teacherViewModel!!) },
            MainScreenRoute.GROUP_SCHEDULE.route to { GroupScheduleScreen(groupViewModel) },
            MainScreenRoute.TEACHER_SEARCH.route to { TeacherSearchScreen(searchViewModel!!) },
        )

        val topAppBar = topBarItems(context, remoteConfigUiState)

        val navHostController = rememberNavController()
        Scaffold(
            topBar = { TopAppBar(topAppBar.first, topAppBar.second) },
            bottomBar = { BottomNavBar(navHostController, items.map { it.bottomNavItem }) }
        ) { paddingValues ->
            NavHostContainer(
                navHostController,
                paddingValues,
                if (role == UserRole.TEACHER)
                    MainScreenRoute.TEACHER_SCHEDULE.route
                else
                    MainScreenRoute.GROUP_SCHEDULE.route,
                routes
            )
        }
    }
}