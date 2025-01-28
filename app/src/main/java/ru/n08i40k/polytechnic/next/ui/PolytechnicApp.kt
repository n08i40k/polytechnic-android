package ru.n08i40k.polytechnic.next.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.Application
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.settings.settings
import ru.n08i40k.polytechnic.next.ui.screen.MainScreen
import ru.n08i40k.polytechnic.next.ui.screen.auth.AuthScreen
import ru.n08i40k.polytechnic.next.utils.app
import ru.n08i40k.polytechnic.next.utils.openLink
import kotlin.system.exitProcess

enum class AppRoute(val route: String) {
    AUTH("auth"),
    MAIN("main")
}

private data class SemVersion(val major: Int, val minor: Int, val patch: Int) :
    Comparable<SemVersion> {
    companion object {
        fun fromString(version: String): SemVersion {
            val numbers = version.split(".").map { it.toInt() }
            assert(numbers.size == 3)

            return SemVersion(numbers[0], numbers[1], numbers[2])
        }
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is SemVersion -> this.major == other.major && this.minor == other.minor && this.patch == other.patch
            else          -> false
        }

    override fun toString(): String {
        return "$major.$minor.$patch"
    }

    override fun compareTo(b: SemVersion): Int {
        val majorDiff = this.major - b.major
        if (majorDiff != 0) return majorDiff

        val minorDiff = this.minor - b.minor
        if (minorDiff != 0) return minorDiff

        return this.patch - b.patch
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + major
        result = 31 * result + minor
        result = 31 * result + patch
        return result
    }
}

@Composable
private fun checkUpdate(): Boolean {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val remoteConfig = app.container.remoteConfig

    val currentVersion = SemVersion.fromString(app.version)
    val minRequiredVersion = SemVersion.fromString(remoteConfig.getString("minVersion"))

    val downloadLink = remoteConfig.getString("downloadLink")

    if (currentVersion < minRequiredVersion) {
        Dialog({ exitProcess(0) }, DialogProperties(false, false)) {
            Card(border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.inverseSurface)) {
                var dialogWidth by remember { mutableStateOf(Dp.Unspecified) }
                val localDensity = LocalDensity.current

                Column(
                    Modifier
                        .padding(10.dp)
                        .onGloballyPositioned {
                            with(localDensity) {
                                dialogWidth = it.size.width.toDp()
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.updater_support_end, minRequiredVersion),
                        Modifier.padding(0.dp, 10.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(stringResource(R.string.updater_body))

                    Spacer(Modifier.height(10.dp))

                    if (dialogWidth != Dp.Unspecified) {
                        Row(Modifier.width(dialogWidth), Arrangement.SpaceBetween) {
                            TextButton({ exitProcess(0) }) {
                                Text(
                                    stringResource(R.string.updater_exit),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                            }
                            TextButton({ context.openLink(downloadLink) }) {
                                Text(
                                    stringResource(R.string.updater_update),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    val latestVersion = SemVersion.fromString(remoteConfig.getString("currVersion"))
    var suppressedVersion by rememberSaveable {
        mutableStateOf(
            runBlocking {
                val data = context.settings.data.map { it.suppressedVersion }.first()

                if (data.isEmpty())
                    "0.0.0"
                else
                    data
            }
        )
    }
    val suppressedSemVer by remember { derivedStateOf { SemVersion.fromString(suppressedVersion) } }

    if (latestVersion > currentVersion && latestVersion != suppressedSemVer) {
        Dialog({ exitProcess(0) }, DialogProperties(false, false)) {
            Card(border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.inverseSurface)) {
                var dialogWidth by remember { mutableStateOf(Dp.Unspecified) }
                val localDensity = LocalDensity.current

                Column(
                    Modifier
                        .padding(10.dp)
                        .onGloballyPositioned {
                            with(localDensity) {
                                dialogWidth = it.size.width.toDp()
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.updater_new_version),
                        Modifier.padding(0.dp, 10.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(stringResource(R.string.updater_body))

                    Spacer(Modifier.height(10.dp))

                    if (dialogWidth != Dp.Unspecified) {
                        Row(Modifier.width(dialogWidth), Arrangement.SpaceBetween) {
                            Row {
                                TextButton({ suppressedVersion = latestVersion.toString() }) {
                                    Text(
                                        stringResource(R.string.updater_no),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                }
                                TextButton({
                                    runBlocking {
                                        context.settings.updateData {
                                            it.toBuilder()
                                                .setSuppressedVersion(latestVersion.toString())
                                                .build()
                                        }
                                    }
                                    suppressedVersion = latestVersion.toString()
                                }) {
                                    Text(
                                        stringResource(R.string.updater_suppress),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                }
                            }

                            TextButton({ context.openLink(downloadLink) }) {
                                Text(
                                    stringResource(R.string.updater_update),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    return true
}

@Composable
fun PolytechnicApp() {
    if (!checkUpdate())
        return

    val navController = rememberNavController()
    val context = LocalContext.current

    remember {
        context.app.events.signOut.subscribe(
            context,
            {
                navController.navigate(AppRoute.AUTH.route) {
                    popUpTo(AppRoute.AUTH.route) { inclusive = true }
                }
            }
        )
    }

    val token = runBlocking {
        context.settings.data.map { it.accessToken }.first()
    }

    NavHost(
        navController,
        startDestination = if (token.isEmpty()) AppRoute.AUTH.route else AppRoute.MAIN.route
    ) {
        composable(AppRoute.AUTH.route) {
            AuthScreen(navController)
        }

        composable(AppRoute.MAIN.route) {
            MainScreen(navController)
        }
    }
}