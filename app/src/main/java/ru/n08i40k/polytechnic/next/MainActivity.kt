package ru.n08i40k.polytechnic.next

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.n08i40k.polytechnic.next.app.NotificationChannels
import ru.n08i40k.polytechnic.next.settings.settings
import ru.n08i40k.polytechnic.next.ui.PolytechnicApp
import ru.n08i40k.polytechnic.next.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        name: String,
        description: String,
        channelId: String
    ) {
        val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = description

        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotificationChannels() {
        if (!hasNotificationPermission())
            return

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
            notificationManager,
            getString(R.string.schedule_channel_name),
            getString(R.string.schedule_channel_description),
            NotificationChannels.SCHEDULE_UPDATE
        )

        createNotificationChannel(
            notificationManager,
            getString(R.string.app_update_channel_name),
            getString(R.string.app_update_channel_description),
            NotificationChannels.APP_UPDATE
        )

//        createNotificationChannel(
//            notificationManager,
//            getString(R.string.lesson_view_channel_name),
//            getString(R.string.lesson_view_channel_description),
//            NotificationChannels.LESSON_VIEW
//        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) createNotificationChannels()
        }

    private fun askNotificationPermission() {
        if (hasNotificationPermission())
            return

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun hasNotificationPermission(): Boolean =
        (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()
        createNotificationChannels()

        lifecycleScope.launch {
            settings.data.first()
        }

        setContent {
            AppTheme {
                Surface {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Top))
                    ) {
                        PolytechnicApp()
                    }
                }
            }
        }
    }
}