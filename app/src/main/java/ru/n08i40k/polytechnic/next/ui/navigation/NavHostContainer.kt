package ru.n08i40k.polytechnic.next.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavHostContainer(
    navHostController: NavHostController,
    padding: PaddingValues,
    startDestination: String,
    routes: Map<String, @Composable () -> Unit>,
    enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)? = null,
    exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)? = null,
    sizeTransform: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null,
) {
    NavHost(
        navController = navHostController,
        modifier = Modifier.padding(padding),
        startDestination = startDestination,
        enterTransition = enterTransition ?: {
            slideIn(
                animationSpec = tween(
                    400,
                    delayMillis = 250,
                    easing = LinearOutSlowInEasing
                )
            ) { fullSize -> IntOffset(0, fullSize.height / 16) } + fadeIn(
                animationSpec = tween(
                    400,
                    delayMillis = 250,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        exitTransition = exitTransition ?: {
            fadeOut(
                animationSpec = tween(
                    250,
                    easing = FastOutSlowInEasing
                )
            )
        },
        sizeTransform = sizeTransform
    ) {
        routes.forEach { route -> composable(route.key) { route.value() } }
    }
}