package ru.n08i40k.polytechnic.next.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.app.appContainer
import ru.n08i40k.polytechnic.next.settings.settings
import ru.n08i40k.polytechnic.next.utils.MyResult
import java.time.Duration
import java.util.logging.Logger

class UpdateFCMTokenWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    private val logger = Logger.getLogger("FCM")

    override suspend fun doWork(): Result {
        val fcmToken = inputData.getString("TOKEN") ?: run {
            logger.warning("FCM token is missing in input data.")
            return Result.failure()
        }
        val accessToken = applicationContext.settings.data.map { it.accessToken }.first()

        if (accessToken.isEmpty()) {
            logger.warning("Access token is empty. Retrying...")
            return Result.retry()
        }

        if (applicationContext
                .appContainer
                .profileRepository
                .setFCMToken(fcmToken) is MyResult.Failure
        ) {
            logger.warning("Failed to set FCM token in the profile repository.")

            return Result.retry()
        }

        return Result.success()
    }

    companion object {
        private const val WORK_TAG = "update-fcm-token"

        fun schedule(context: Context, token: String) {
            runBlocking {
                context.settings.updateData {
                    it.toBuilder().setFcmToken(token).build()
                }
            }

            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(WORK_TAG)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<UpdateFCMTokenWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
                .setInputData(workDataOf("TOKEN" to token))
                .addTag(WORK_TAG)
                .build()

            workManager.enqueue(request)
        }
    }
}