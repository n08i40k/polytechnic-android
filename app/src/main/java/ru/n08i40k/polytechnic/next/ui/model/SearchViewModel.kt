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
import java.util.Date
import javax.inject.Inject

sealed interface SearchUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : SearchUiState

    data class HasData(
        val teacher: GroupOrTeacher,
        val cacheDate: CacheDate,
        val lastUpdateAt: Long,
        override val isLoading: Boolean
    ) : SearchUiState
}

private data class SearchViewModelState(
    val teacher: GroupOrTeacher? = null,
    val updateDates: CacheDate? = null,
    val lastUpdateAt: Long = 0,
    val isLoading: Boolean = false
) {
    fun toUiState(): SearchUiState =
        if (teacher == null) SearchUiState.NoData(isLoading)
        else SearchUiState.HasData(teacher, updateDates!!, lastUpdateAt, isLoading)
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val scheduleRepository = appContainer.scheduleRepository
    private val networkCacheRepository = appContainer.networkCacheRepository

    private val state = MutableStateFlow(SearchViewModelState(isLoading = true))

    val uiState = state
        .map(SearchViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, state.value.toUiState())

    private var teacherName: String? = null

    init {
        refresh()
    }

    fun set(name: String?) {
        teacherName = name
        refresh()
    }

    fun refresh() {
        state.update { it.copy(isLoading = true) }

        if (teacherName == null) {
            state.update {
                it.copy(
                    teacher = null,
                    updateDates = null,
                    lastUpdateAt = 0,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            scheduleRepository.getTeacher(teacherName!!).let { result ->
                state.update {
                    when (result) {
                        is MyResult.Success -> it.copy(
                            teacher = result.data,
                            updateDates = networkCacheRepository.getUpdateDates(),
                            lastUpdateAt = Date().time,
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
