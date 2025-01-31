package ru.n08i40k.polytechnic.next.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import ru.n08i40k.polytechnic.next.Application
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.app.NotificationChannels
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.utils.MyResult
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.fmtAsClock
import ru.n08i40k.polytechnic.next.utils.now
import java.util.logging.Logger

class DayViewService : Service() {
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    companion object {
        private val logger = Logger.getLogger("DayView")

        private const val NOTIFICATION_MAIN_ID = 3141_00
        private const val NOTIFICATION_END_ID = 3141_59

        private const val UPDATE_INTERVAL_MILLIS = 1_000L

        fun start(app: Application) {
            if (!app.hasNotificationPermission) {
                logger.warning("Cannot start service, because app don't have notifications permission!")
                return
            }

            val intent = Intent(app, DayViewService::class.java)

            app.stopService(intent)
            startForegroundService(app, intent)
        }
    }

    private lateinit var day: Day
    private val handler = Handler(Looper.getMainLooper())

    private fun onLessonsEnd() {
        notificationManager.notify(
            NOTIFICATION_END_ID,
            NotificationCompat
                .Builder(this, NotificationChannels.DAY_VIEW)
                .setSmallIcon(R.drawable.schedule)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(getString(R.string.day_view_end_title))
                .setContentText(getString(R.string.day_view_end_description))
                .build()
        )

        handler.removeCallbacks(runnable)

        stopSelf()
    }

    private val runnable = object : Runnable {
        override fun run() {
            val (currentIndex, current) = day.currentKV ?: (null to null)
            val (nextIndex, distanceToNext) = day.distanceToNext(currentIndex) ?: (null to null)
            if (current == null && nextIndex == null) {
                onLessonsEnd()
                return
            }

            handler.postDelayed(this, UPDATE_INTERVAL_MILLIS)

            val context = this@DayViewService

            val next = nextIndex?.let { day.lessons[nextIndex] }
            val nextName = next?.getShortName(context) ?: getString(R.string.day_view_lessons_end)

            val nowMinutes = LocalDateTime.now().dayMinutes
            val eventMinutes = (current?.time?.end ?: next!!.time.start).dayMinutes

            // Если следующая пара - первая.
            // Пока что вариантов, когда текущая пара null, а следующая нет я не видел.
            if (current == null) {
                notificationManager.notify(
                    NOTIFICATION_MAIN_ID,
                    createNotification(
                        getString(
                            R.string.day_view_wait_for_begin_title,
                            (eventMinutes - nowMinutes) / 60,
                            (eventMinutes - nowMinutes) % 60
                        ),
                        getString(
                            R.string.day_view_going_description,
                            getString(R.string.day_view_not_started),
                            eventMinutes.fmtAsClock(),
                            nextName,
                        ),
                    )
                )

                return
            }

            notificationManager.notify(
                NOTIFICATION_MAIN_ID,
                createNotification(
                    getString(
                        R.string.day_view_going_title,
                        (eventMinutes - nowMinutes) / 60,
                        (eventMinutes - nowMinutes) % 60
                    ),
                    getString(
                        R.string.day_view_going_description,
                        current.getShortName(context),
                        eventMinutes.fmtAsClock(),
                        nextName,
                    ),
                )
            )
        }
    }

    private lateinit var notificationManager: NotificationManager

    private fun createNotification(
        title: String,
        description: String
    ): Notification {
        return NotificationCompat
            .Builder(this, NotificationChannels.DAY_VIEW)
            .setSmallIcon(R.drawable.schedule)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private suspend fun loadSchedule(): Boolean {
        val profileRepository = appContainer.profileRepository
        val scheduleRepository = appContainer.scheduleRepository

        val profile = when (val result = profileRepository.getProfile()) {
            is MyResult.Success -> result.data
            else                -> {
                logger.warning("Cannot start service, because get profile request failed!")
                return false
            }
        }

        val schedule = when (
            val result = when (profile.role) {
                UserRole.TEACHER -> {
                    // TODO: implement schedule breaks for teachers on server-side.
                    return false
                }

                else             -> scheduleRepository.getGroup()
            }
        ) {
            is MyResult.Success -> result.data
            else                -> {
                logger.warning("Cannot start service, because get schedule request failed!")
                return false
            }
        }

        val currentDay = schedule.current

        if (currentDay == null || currentDay.lessons.isEmpty()) {
            logger.warning("Cannot start service, because no lessons today!")
            return false
        }

        if (Clock.System.now() > currentDay.lessons.first().time.start && currentDay.current == null) {
            logger.warning("Cannot start service, because it started after lessons end!")
            return false
        }

        this.day = currentDay
        return true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = createNotification(
            getString(R.string.day_view_title),
            getString(R.string.day_view_description)
        )
        startForeground(NOTIFICATION_MAIN_ID, notification)

        coroutineScope
            .launch {
                if (!loadSchedule()) {
                    stopSelf()
                    return@launch
                }

                this@DayViewService.handler.removeCallbacks(runnable)
                this@DayViewService.runnable.run()
            }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}