package ru.n08i40k.polytechnic.next.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.n08i40k.polytechnic.next.CacheDate
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.utils.MyResult
import javax.inject.Inject

sealed interface TeacherUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : TeacherUiState

    data class HasData(
        val teacher: GroupOrTeacher,
        val cacheDate: CacheDate,
        val lastUpdateAt: Long,
        override val isLoading: Boolean
    ) : TeacherUiState
}

private data class TeacherViewModelState(
    val teacher: GroupOrTeacher? = null,
    val updateDates: CacheDate? = null,
    val lastUpdateAt: Long = 0,
    val isLoading: Boolean = false
) {
    fun toUiState(): TeacherUiState = when (teacher) {
        null -> TeacherUiState.NoData(isLoading)
        else -> TeacherUiState.HasData(teacher, updateDates!!, lastUpdateAt, isLoading)
    }
}

@HiltViewModel
class TeacherViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val scheduleRepository = appContainer.scheduleRepository
    private val networkCacheRepository = appContainer.networkCacheRepository

    private val state = MutableStateFlow(TeacherViewModelState(isLoading = true))

    val uiState = state
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), state.value.toUiState())

    init {
        refresh()
    }

    fun refresh() {
        state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            scheduleRepository.getTeacher("self").let { result ->
                state.update {
                    when (result) {
                        is MyResult.Success -> it.copy(
                            teacher = result.data,
                            updateDates = networkCacheRepository.getUpdateDates(),
                            lastUpdateAt = System.currentTimeMillis(),
                            isLoading = false
                        )

                        is MyResult.Failure -> it.copy(
                            teacher = null,
                            updateDates = null,
                            lastUpdateAt = 0,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
