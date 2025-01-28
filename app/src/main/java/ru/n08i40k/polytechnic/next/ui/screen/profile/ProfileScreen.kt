package ru.n08i40k.polytechnic.next.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.ui.model.ProfileUiState
import ru.n08i40k.polytechnic.next.ui.model.ProfileViewModel
import ru.n08i40k.polytechnic.next.ui.widgets.LoadingContent

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val onRefresh: () -> Unit = { profileViewModel.refresh() }

    LoadingContent(
        empty = when (uiState) {
            is ProfileUiState.NoData -> uiState.isLoading
            is ProfileUiState.HasData -> false
        },
        loading = uiState.isLoading,
        onRefresh = { profileViewModel.refresh() },
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState) {
            is ProfileUiState.HasData -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val context = LocalContext.current

                    ProfileCard((uiState as ProfileUiState.HasData).profile) {
                        runBlocking { context.appContainer.networkCacheRepository.clear() }

                        profileViewModel.refresh()
                    }
                }
            }

            is ProfileUiState.NoData -> {
                if (!uiState.isLoading) {
                    TextButton(onClick = onRefresh, modifier = Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.reload), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}