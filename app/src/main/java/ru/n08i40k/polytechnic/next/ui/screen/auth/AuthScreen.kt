package ru.n08i40k.polytechnic.next.ui.screen.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import ru.n08i40k.polytechnic.next.ui.AppRoute
import ru.n08i40k.polytechnic.next.ui.helper.PushSnackbar
import ru.n08i40k.polytechnic.next.ui.helper.SnackbarBox
import ru.n08i40k.polytechnic.next.worker.UpdateFCMTokenWorker


enum class AuthRoute(val route: String) {
    SignUp("sign-up"),
    SignIn("sign-in"),
}

@Composable
private fun FormWrapper(
    onWidthChange: (Dp) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.inverseSurface)) {
            val localDensity = LocalDensity.current
            Box(
                Modifier
                    .padding(10.dp)
                    .onGloballyPositioned {
                        with(localDensity) {
                            onWidthChange(it.size.width.toDp())
                        }
                    }
                /*.animateContentSize()*/,
                content = content
            )
        }
    }
}

@Composable
private fun AuthForm(parentNavController: NavController, pushSnackbar: PushSnackbar) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val switch: () -> Unit = {
        navController.navigate(
            if (navController.currentDestination?.route == AuthRoute.SignUp.route)
                AuthRoute.SignIn.route
            else
                AuthRoute.SignUp.route
        )
    }

    val finish: () -> Unit = {
        parentNavController.navigate(AppRoute.MAIN.route) {
            popUpTo(AppRoute.AUTH.route) { inclusive = true }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(object :
            OnCompleteListener<String> {
            override fun onComplete(token: Task<String?>) {
                if (!token.isSuccessful)
                    return

                UpdateFCMTokenWorker.schedule(context, token.result!!)
            }
        })
    }

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = "sign-up",
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 700,
                    delayMillis = 800,
                    easing = LinearOutSlowInEasing
                )
            ) + scaleIn(
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = 700,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        exitTransition = {
            slideOut(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = LinearEasing
                )
            ) { fullSize -> IntOffset(0, fullSize.height / 16) } + fadeOut(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = LinearEasing
                )
            )
        },
    ) {
        composable(AuthRoute.SignUp.route) {
            var width by remember { mutableStateOf(Dp.Unspecified) }

            FormWrapper({ width = it }) {
                SignUpForm(pushSnackbar, switch, finish, width)
            }
        }

        composable(AuthRoute.SignIn.route) {
            var width by remember { mutableStateOf(Dp.Unspecified) }

            FormWrapper({ width = it }) {
                SignInCard(pushSnackbar, switch, finish, width)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    SnackbarBox {
        Box(contentAlignment = Alignment.Center) {
            AuthForm(navController, it)
        }
    }
}