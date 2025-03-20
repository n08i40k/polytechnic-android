package ru.n08i40k.polytechnic.next.ui.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.ui.model.TeacherUiState
import ru.n08i40k.polytechnic.next.ui.model.TeacherViewModel
import ru.n08i40k.polytechnic.next.ui.widgets.LoadingContent
import ru.n08i40k.polytechnic.next.ui.widgets.schedule.SchedulePager
import ru.n08i40k.polytechnic.next.ui.widgets.schedule.UpdateInfo
import ru.n08i40k.polytechnic.next.utils.rememberUpdatedLifecycleOwner

@Composable
fun TeacherScheduleScreen(viewModel: TeacherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refresh: () -> Unit = { viewModel.refresh() }

    // auto-refresh every 2 minutes
    LaunchedEffect(uiState) {
        delay(120_000)
        refresh()
    }

    val lifecycleOwner = rememberUpdatedLifecycleOwner()

    // обновление при развороте приложения
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> refresh()
                else                      -> Unit
            }
        }

        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    LoadingContent(
        empty = uiState is TeacherUiState.NoData && uiState.isLoading,
        loading = uiState.isLoading,
        onRefresh = refresh,
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState) {
            is TeacherUiState.HasData -> {
                Column(Modifier.padding(20.dp), Arrangement.spacedBy(10.dp)) {
                    val data = uiState as TeacherUiState.HasData

                    UpdateInfo(data.lastUpdateAt, data.cacheDate)
                    SchedulePager(data.teacher)
                }
            }

            else                      -> {
                if (!uiState.isLoading) {
                    TextButton(refresh, Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.reload), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}