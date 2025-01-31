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
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.utils.MyResult
import ru.n08i40k.polytechnic.next.utils.SingleHook
import javax.inject.Inject

sealed interface ProfileUiState {
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean
    ) : ProfileUiState

    data class HasData(
        override val isLoading: Boolean,
        val profile: Profile
    ) : ProfileUiState
}

private data class ProfileViewModelState(
    val profile: Profile? = null,
    val isLoading: Boolean = false
) {
    fun toUiState(): ProfileUiState = when (profile) {
        null -> ProfileUiState.NoData(isLoading)
        else -> ProfileUiState.HasData(isLoading, profile)
    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val repository = appContainer.profileRepository

    private val state = MutableStateFlow(ProfileViewModelState(isLoading = true))

    val uiState = state
        .map(ProfileViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, state.value.toUiState())

    init {
        refresh()
    }

    fun refresh(): SingleHook<Profile?> {
        val singleHook = SingleHook<Profile?>()

        state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getProfile().let { result ->
                state.update {
                    when (result) {
                        is MyResult.Failure -> it.copy(null, false)
                        is MyResult.Success -> it.copy(result.data, false)
                    }
                }

                singleHook.resolve(
                    if (result is MyResult.Success)
                        result.data
                    else
                        null
                )
            }
        }

        return singleHook
    }
}