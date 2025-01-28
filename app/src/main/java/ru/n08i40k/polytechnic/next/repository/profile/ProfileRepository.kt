package ru.n08i40k.polytechnic.next.repository.profile

import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.utils.MyResult

interface ProfileRepository {
    suspend fun getProfile(): MyResult<Profile>

    suspend fun setFCMToken(token: String): MyResult<Unit>

    suspend fun signOut()
}