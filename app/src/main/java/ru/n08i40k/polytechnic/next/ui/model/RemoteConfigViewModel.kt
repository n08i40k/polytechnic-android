package ru.n08i40k.polytechnic.next.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.n08i40k.polytechnic.next.app.AppContainer
import java.util.logging.Logger
import javax.inject.Inject

data class RemoteConfigUiState(
    val minVersion: String,
    val currVersion: String,
    val serverVersion: String,
    val downloadLink: String,
    val telegramLink: String,
    val linkUpdateDelay: Long,
)

@HiltViewModel
class RemoteConfigViewModel @Inject constructor(
    appContainer: AppContainer
) : ViewModel() {
    private val remoteConfig = appContainer.remoteConfig

    private val state = MutableStateFlow(
        RemoteConfigUiState(
            minVersion = remoteConfig.getString("minVersion"),
            currVersion = remoteConfig.getString("currVersion"),
            downloadLink = remoteConfig.getString("downloadLink"),
            telegramLink = remoteConfig.getString("telegramLink"),
            serverVersion = remoteConfig.getString("serverVersion"),
            linkUpdateDelay = remoteConfig.getLong("linkUpdateDelay"),
        )
    )

    val uiState = state
        .stateIn(viewModelScope, SharingStarted.Eagerly, state.value)

    init {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    state.update {
                        it.copy(
                            minVersion = remoteConfig.getString("minVersion"),
                            currVersion = remoteConfig.getString("currVersion"),
                            downloadLink = remoteConfig.getString("downloadLink"),
                            telegramLink = remoteConfig.getString("telegramLink"),
                            serverVersion = remoteConfig.getString("serverVersion"),
                            linkUpdateDelay = remoteConfig.getLong("linkUpdateDelay"),
                        )
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Logger.getLogger("RemoteConfigViewModel")
                    .severe("Failed to fetch RemoteConfig update!")
            }
        })
    }
}
