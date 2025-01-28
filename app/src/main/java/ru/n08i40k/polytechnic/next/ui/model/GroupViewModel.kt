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
import ru.n08i40k.polytechnic.next.UpdateDates
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.utils.MyResult
import java.util.Date
import javax.inject.Inject

sealed interface GroupUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : GroupUiState

    data class HasData(
        val group: GroupOrTeacher,
        val updateDates: UpdateDates,
        val lastUpdateAt: Long,
        override val isLoading: Boolean
    ) : GroupUiState
}

private data class GroupViewModelState(
    val group: GroupOrTeacher? = null,
    val updateDates: UpdateDates? = null,
    val lastUpdateAt: Long = 0,
    val isLoading: Boolean = false
) {
    fun toUiState(): GroupUiState =
        if (group == null)
            GroupUiState.NoData(isLoading)
        else
            GroupUiState.HasData(group, updateDates!!, lastUpdateAt, isLoading)
}

@HiltViewModel
class GroupViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val scheduleRepository = appContainer.scheduleRepository
    private val networkCacheRepository = appContainer.networkCacheRepository

    private val state = MutableStateFlow(GroupViewModelState(isLoading = true))

    val uiState = state
        .map(GroupViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Companion.Eagerly, state.value.toUiState())

    init {
        refresh()
    }

    fun refresh() {
        state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = scheduleRepository.getGroup()

            state.update {
                when (result) {
                    is MyResult.Success -> it.copy(
                        group = result.data,
                        updateDates = networkCacheRepository.getUpdateDates(),
                        lastUpdateAt = Date().time,
                        isLoading = false
                    )

                    is MyResult.Failure -> it.copy(
                        group = null,
                        updateDates = null,
                        lastUpdateAt = 0,
                        isLoading = false
                    )
                }
            }
        }
    }
}
