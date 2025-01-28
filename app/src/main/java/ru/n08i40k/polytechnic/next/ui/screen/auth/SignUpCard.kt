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
import ru.n08i40k.polytechnic.next.ui.screen.auth.signup.ManualPage
import ru.n08i40k.polytechnic.next.ui.screen.auth.signup.VKPage
import ru.n08i40k.polytechnic.next.ui.widgets.OneTapComplete

private enum class SignUpPage(val route: String) {
    SELECT("select"),
    MANUAL("manual"),
    VK("vk")
}

@Preview(showBackground = true)
@Composable
private fun SelectSignUpMethod(
    onSelected: (SignUpPage, String?) -> Unit = { _, _ -> },
    switch: () -> Unit = {}
) {
    val modifier = Modifier.width(240.dp)

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.sign_up_title),
            Modifier.padding(10.dp),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold
        )

        Box(modifier, contentAlignment = Alignment.Center) {
            Button({ onSelected(SignUpPage.MANUAL, null) }, modifier) {
                Text(stringResource(R.string.sign_up_manual), fontWeight = FontWeight.Bold)
            }
            Row(modifier.padding(10.dp, 0.dp)) {
                Icon(
                    Icons.Filled.Create,
                    stringResource(R.string.cd_manual_icon),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        OneTapComplete(onAuth = { onSelected(SignUpPage.VK, it) }, onFail = {})

        Box(modifier, contentAlignment = Alignment.Center) {
            HorizontalDivider()
            Text(
                stringResource(R.string.or_divider),
                Modifier.background(CardDefaults.cardColors().containerColor)
            )
        }

        Button(switch, modifier) {
            Text(stringResource(R.string.sign_up_already_registered))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpForm(
    pushSnackbar: PushSnackbar = { msg, dur -> },
    toSignIn: () -> Unit = {},
    toApp: () -> Unit = {},
    parentWidth: Dp = Dp.Unspecified,
) {
    val navHostController = rememberNavController()

    val toSelect: () -> Unit = {
        navHostController.navigate(SignUpPage.SELECT.route) {
            popUpTo(SignUpPage.SELECT.route) { inclusive = true }
        }
    }

    var accessToken by remember { mutableStateOf<String?>(null) }

    NavHostContainer(
        navHostController,
        PaddingValues(0.dp),
        SignUpPage.SELECT.route,
        mapOf<String, @Composable () -> Unit>(
            SignUpPage.SELECT.route to {
                SelectSignUpMethod(
                    { page, token ->
                        navHostController.navigate(page.route)
                        accessToken = token
                    },
                    toSignIn
                )
            },
            SignUpPage.MANUAL.route to {
                ManualPage(pushSnackbar, toApp, toSelect, parentWidth)
            },
            SignUpPage.VK.route to {
                VKPage(accessToken!!, pushSnackbar, toApp, toSelect, parentWidth)
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