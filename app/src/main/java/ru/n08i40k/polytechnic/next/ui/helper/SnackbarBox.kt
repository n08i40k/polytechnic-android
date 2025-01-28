package ru.n08i40k.polytechnic.next.ui.helper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

typealias PushSnackbar = (String, SnackbarDuration) -> Unit

@Composable
fun SnackbarBox(modifier: Modifier = Modifier, content: @Composable (PushSnackbar) -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val pushSnackbar: PushSnackbar = { msg, duration ->
        coroutineScope.launch { snackbarHostState.showSnackbar(msg, duration = duration) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
    ) {
        Box(
            modifier
                .fillMaxSize()
                .padding(it)) { content(pushSnackbar) }
    }
}