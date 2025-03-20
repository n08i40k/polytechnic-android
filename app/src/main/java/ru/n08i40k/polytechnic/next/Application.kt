package ru.n08i40k.polytechnic.next

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.vk.id.VKID
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.proto.settings
import ru.n08i40k.polytechnic.next.proto.settings_v0
import ru.n08i40k.polytechnic.next.utils.Observable
import ru.n08i40k.polytechnic.next.worker.UpdateFCMTokenWorker
import ru.n08i40k.polytechnic.next.worker.UpdateLinkWorker
import java.util.logging.Logger
import javax.inject.Inject

data class AppEvents(
    val signOut: Observable<Unit> = Observable<Unit>()
)

@HiltAndroidApp
class Application : Application() {
    @Inject
    lateinit var container: AppContainer

    val events = AppEvents()

    val version
        get() = applicationContext.packageManager
            .getPackageInfo(this.packageName, 0)
            .versionName!!

    // permissions
    val hasNotificationPermission: Boolean
        get() =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                    || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    private fun setupFirebase() {
        fun scheduleUpdateLinkWorker() {
            container.remoteConfig.activate().addOnCompleteListener {
                UpdateLinkWorker.schedule(this@Application)
            }
        }

        fun fixupToken() {
            if (runBlocking { settings.data.map { it.fcmToken }.first() }.isNotEmpty())
                return

            FirebaseMessaging.getInstance().token.addOnCompleteListener(object :
                OnCompleteListener<String> {
                override fun onComplete(token: Task<String?>) {
                    if (!token.isSuccessful)
                        return

                    UpdateFCMTokenWorker.schedule(applicationContext, token.result!!)
                }
            })
        }

        val remoteConfig = container.remoteConfig

        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                scheduleUpdateLinkWorker()
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Logger.getLogger("Application")
                    .severe("Failed to fetch RemoteConfig update!")
            }
        })

        scheduleUpdateLinkWorker()
        fixupToken()
    }

    override fun onCreate() {
        super.onCreate()

        runBlocking { fixupSettings() }

        VKID.init(this)

        setupFirebase()
    }

    private suspend fun fixupSettings() {
        val accessToken = this.settings_v0.data.map { it.accessToken }.first()

        if (accessToken.isEmpty())
            return

        val userId = this.settings_v0.data.map { it.userId }.first()
        val group = this.settings_v0.data.map { it.group }.first()
        val version = this.settings_v0.data.map { it.version }.first()
        val fcmToken = this.settings_v0.data.map { it.fcmToken }.first()

        this.settings.updateData {
            it
                .toBuilder()
                .setUserId(userId)
                .setAccessToken(accessToken)
                .setGroup(group)
                .setVersion(version)
                .setFcmToken(fcmToken)
                .build()
        }

        this.settings_v0.updateData { it.toBuilder().clear().build() }
    }
}