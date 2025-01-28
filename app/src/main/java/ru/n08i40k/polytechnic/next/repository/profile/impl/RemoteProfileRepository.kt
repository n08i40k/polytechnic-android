package ru.n08i40k.polytechnic.next.repository.profile.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.request.fcm.FcmSetToken
import ru.n08i40k.polytechnic.next.network.request.profile.ProfileMe
import ru.n08i40k.polytechnic.next.network.tryFuture
import ru.n08i40k.polytechnic.next.repository.profile.ProfileRepository
import ru.n08i40k.polytechnic.next.settings.settings
import ru.n08i40k.polytechnic.next.utils.MyResult
import ru.n08i40k.polytechnic.next.utils.app

class RemoteProfileRepository(private val container: AppContainer) : ProfileRepository {
    override suspend fun getProfile(): MyResult<Profile> {
        return withContext(Dispatchers.IO) {
            tryFuture(container.context) {
                ProfileMe(
                    container,
                    it,
                    it
                )
            }
        }
    }

    override suspend fun setFCMToken(token: String): MyResult<Unit> =
        withContext(Dispatchers.IO) {
            tryFuture(container.context) {
                FcmSetToken(
                    container,
                    token,
                    it,
                    it
                )
            }
        }

    override suspend fun signOut() {
        val context = container.context

        container.context.settings.updateData {
            it
                .toBuilder()
                .clear()
                .build()
        }

        context.app.events.signOut.next(Unit)

//        context.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)
//        val pm = context.packageManager
//        val intent = pm.getLaunchIntentForPackage(context.packageName)
//        val mainIntent = Intent.makeRestartActivityTask(intent?.component)
//        context.startActivity(mainIntent)
//        Runtime.getRuntime().exit(0)
    }
}