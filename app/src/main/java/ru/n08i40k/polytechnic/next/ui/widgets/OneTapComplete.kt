package ru.n08i40k.polytechnic.next.ui.widgets

import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.common.OneTapStyle
import com.vk.id.onetap.common.button.style.OneTapButtonCornersStyle
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario
import ru.n08i40k.polytechnic.next.network.request.vkid.VKIDOAuth
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.UUID

private data class PKCE(
    val codeVerifier: String,
    val state: String,
    val codeChallenge: String
) {
    companion object {
        private val ALLOWED_CHARS = ('A'..'Z') + ('a'..'z') + ('0'..'9') + '_' + '-'

        fun create(): PKCE {
            val codeVerifier = List(64) { ALLOWED_CHARS.random() }.joinToString("")

            val sha256Digester = MessageDigest.getInstance("SHA-256")
            sha256Digester.update(codeVerifier.toByteArray(Charset.forName("ISO_8859_1")))

            val codeChallenge = Base64.encodeToString(
                sha256Digester.digest(),
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )

            return PKCE(codeVerifier, UUID.randomUUID().toString(), codeChallenge)
        }
    }
}

private val PkceSaver = listSaver<MutableState<PKCE>, String>(
    save = {
        val value by it

        listOf(value.codeVerifier, value.state, value.codeChallenge)
    },
    restore = { mutableStateOf(PKCE(it[0], it[1], it[2])) }
)

@Composable
fun OneTapComplete(modifier: Modifier = Modifier, onAuth: (String) -> Unit, onFail: () -> Unit) {
    val context = LocalContext.current

    var pkce by rememberSaveable(saver = PkceSaver) { mutableStateOf(PKCE.create()) }
    val uiParams = VKIDAuthUiParams.Builder().apply {
        state = pkce.state
        codeChallenge = pkce.codeChallenge
    }.build()

    OneTap(
        modifier = modifier,
        onAuth = { _, _ -> },
        onAuthCode = { authCode, isComplete ->
            VKIDOAuth(
                VKIDOAuth.RequestDto(authCode.code, pkce.codeVerifier, authCode.deviceId),
                { pkce = PKCE.create(); onAuth(it.accessToken) },
                { pkce = PKCE.create() }
            ).send(context)
        },
        onFail = { _, _ -> pkce = PKCE.create(); onFail() },
        style = OneTapStyle.Dark(cornersStyle = OneTapButtonCornersStyle.Round),
        scenario = OneTapTitleScenario.SignIn,
        authParams = uiParams
    )
}