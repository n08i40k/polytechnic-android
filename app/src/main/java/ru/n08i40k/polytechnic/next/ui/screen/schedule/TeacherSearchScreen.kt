package ru.n08i40k.polytechnic.next.ui.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.ui.model.SearchUiState
import ru.n08i40k.polytechnic.next.ui.model.SearchViewModel
import ru.n08i40k.polytechnic.next.ui.widgets.LoadingContent
import ru.n08i40k.polytechnic.next.ui.widgets.schedule.SchedulePager
import ru.n08i40k.polytechnic.next.ui.widgets.selector.TeacherNameSelector
import ru.n08i40k.polytechnic.next.utils.rememberUpdatedLifecycleOwner

@Composable
fun TeacherSearchScreen(viewModel: SearchViewModel) {
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

    var teacherName by remember { mutableStateOf<String?>(null) }

    LoadingContent(
        empty = uiState is SearchUiState.NoData && uiState.isLoading,
        loading = uiState.isLoading,
        onRefresh = refresh,
        verticalArrangement = Arrangement.Top
    ) {
        TeacherNameSelector(
            teacherName,
            {
                teacherName = it
                viewModel.set(teacherName)
            },
            Modifier.fillMaxWidth(),
            false,
            uiState.isLoading
        )

        Spacer(Modifier.height(10.dp))

        when (uiState) {
            is SearchUiState.HasData -> {
                Column {
                    val data = uiState as SearchUiState.HasData

                    UpdateInfo(data.lastUpdateAt, data.cacheDate)
                    Spacer(Modifier.height(10.dp))
                    SchedulePager(data.teacher)
                }
            }

            else                     -> {
                if (!uiState.isLoading) {
                    TextButton(refresh, Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.reload), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}