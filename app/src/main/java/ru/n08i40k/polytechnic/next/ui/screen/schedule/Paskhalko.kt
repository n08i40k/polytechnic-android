package ru.n08i40k.polytechnic.next.ui.screen.schedule

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import ru.n08i40k.polytechnic.next.R

@Preview(showSystemUi = true, showBackground = true)
@Composable
internal fun PaskhalkoDialog() {
    Dialog(onDismissRequest = {}) {
        Image(
            painter = painterResource(R.drawable.paskhalko),
            contentDescription = "paskhalko"
        )
    }
}