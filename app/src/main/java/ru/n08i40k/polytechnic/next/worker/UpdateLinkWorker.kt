package ru.n08i40k.polytechnic.next.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.settings.settings
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class UpdateLinkWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    private val logger = Logger.getLogger("Link")

    override suspend fun doWork(): Result {
        val accessToken = applicationContext.settings.data.map { it.accessToken }.first()

        if (accessToken.isEmpty()) {
            logger.warning("Access token is empty. Retrying...")
            return Result.retry()
        }

        applicationContext
            .appContainer
            .scheduleRepository
            .getGroup()

        return Result.success()
    }

    companion object {
        private const val WORK_TAG = "schedule-update"

        fun schedule(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(WORK_TAG)

            val remoteConfig = context.appContainer.remoteConfig
            val updateDelay = remoteConfig.getLong("linkUpdateDelay")

            if (updateDelay == 0L)
                return

            val workRequest = PeriodicWorkRequest.Builder(
                UpdateLinkWorker::class.java,
                updateDelay.coerceAtLeast(15), TimeUnit.MINUTES
            )
                .addTag(WORK_TAG)
                .build()

            workManager.enqueue(workRequest)
        }
    }
}