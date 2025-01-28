package ru.n08i40k.polytechnic.next.repository.profile.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.repository.profile.ProfileRepository
import ru.n08i40k.polytechnic.next.utils.MyResult

class MockProfileRepository : ProfileRepository {
    private var getCounter = 0

    companion object {
        val profile = Profile(
            "66db32d24030a07e02d974c5",
            "n08i40k",
            "ИС-214/23",
            UserRole.STUDENT
        )
    }

    override suspend fun getProfile(): MyResult<Profile> =
        withContext(Dispatchers.IO) {
            delay(1500)

            if (++getCounter % 3 == 0)
                MyResult.Failure(Exception())
            else
                MyResult.Success(profile)
        }

    override suspend fun setFCMToken(token: String): MyResult<Unit> =
        MyResult.Success(Unit)

    override suspend fun signOut() {

    }
}