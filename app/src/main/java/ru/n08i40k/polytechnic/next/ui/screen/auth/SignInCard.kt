package ru.n08i40k.polytechnic.next.ui.screen.auth

import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.ui.helper.PushSnackbar
import ru.n08i40k.polytechnic.next.ui.navigation.NavHostContainer
import ru.n08i40k.polytechnic.next.ui.screen.auth.signin.ManualPage
import ru.n08i40k.polytechnic.next.ui.screen.auth.signin.VKOneTap


private enum class SignInPage(val route: String) {
    SELECT("select"),
    MANUAL("manual"),
}

@Preview(showBackground = true)
@Composable
private fun SelectSignInMethod(
    onSelected: (SignInPage) -> Unit = {},
    switch: () -> Unit = {},
    toApp: () -> Unit = {},
    pushSnackbar: PushSnackbar = { _, _ -> },
) {
    val modifier = Modifier.width(240.dp)
    var vkId by remember { mutableStateOf(false) }

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.sign_in_title),
            Modifier.padding(10.dp),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold
        )

        Box(modifier, contentAlignment = Alignment.Center) {
            Button({ onSelected(SignInPage.MANUAL) }, modifier, !vkId) {
                Text(stringResource(R.string.sign_in_manual), fontWeight = FontWeight.Bold)
            }
            Row(modifier.padding(10.dp, 0.dp)) {
                Icon(
                    Icons.Filled.Create,
                    stringResource(R.string.cd_manual_icon),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        VKOneTap(toApp, pushSnackbar) { vkId = it }

        Box(modifier, contentAlignment = Alignment.Center) {
            HorizontalDivider()
            Text(
                stringResource(R.string.or_divider),
                Modifier.background(CardDefaults.cardColors().containerColor)
            )
        }

        Button(switch, modifier, !vkId) {
            Text(stringResource(R.string.sign_in_not_registered))
        }
    }
}

@Composable
fun SignInCard(
    pushSnackbar: PushSnackbar,
    toSignUp: () -> Unit,
    toApp: () -> Unit,
    parentWidth: Dp,
) {
    val navHostController = rememberNavController()

    val toSelect: () -> Unit = {
        navHostController.navigate(SignInPage.SELECT.route) {
            popUpTo(SignInPage.SELECT.route) { inclusive = true }
        }
    }

    NavHostContainer(
        navHostController,
        PaddingValues(0.dp),
        SignInPage.SELECT.route,
        mapOf<String, @Composable () -> Unit>(
            SignInPage.SELECT.route to {
                SelectSignInMethod(
                    { page -> navHostController.navigate(page.route) },
                    toSignUp,
                    toApp,
                    pushSnackbar
                )
            },
            SignInPage.MANUAL.route to {
                ManualPage(
                    pushSnackbar,
                    toApp,
                    toSelect,
                    parentWidth
                )
            }
        ),
        enterTransition = {
            slideIn(
                animationSpec = tween(
                    400,
                    delayMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            ) { fullSize -> IntOffset(0, -fullSize.height / 16) } + fadeIn(
                animationSpec = tween(
                    400,
                    delayMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        sizeTransform = {
            SizeTransform { initialSize, targetSize ->
                keyframes {
                    durationMillis = 250
                    delayMillis = 250
                }
            }
        }
    )
}