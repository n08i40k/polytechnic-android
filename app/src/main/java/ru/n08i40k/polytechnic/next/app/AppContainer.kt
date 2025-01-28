package ru.n08i40k.polytechnic.next.app

import android.app.Application
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.n08i40k.polytechnic.next.repository.cache.NetworkCacheRepository
import ru.n08i40k.polytechnic.next.repository.cache.impl.LocalNetworkCacheRepository
import ru.n08i40k.polytechnic.next.repository.cache.impl.MockNetworkCacheRepository
import ru.n08i40k.polytechnic.next.repository.profile.ProfileRepository
import ru.n08i40k.polytechnic.next.repository.profile.impl.MockProfileRepository
import ru.n08i40k.polytechnic.next.repository.profile.impl.RemoteProfileRepository
import ru.n08i40k.polytechnic.next.repository.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.repository.schedule.impl.RemoteScheduleRepository
import javax.inject.Singleton

interface AppContainer {
    val context: Context

    val remoteConfig: FirebaseRemoteConfig

    val profileRepository: ProfileRepository
    val scheduleRepository: ScheduleRepository
    val networkCacheRepository: NetworkCacheRepository
}

abstract class SharedAppContainer(override val context: Context) : AppContainer {
    override val remoteConfig: FirebaseRemoteConfig by lazy { Firebase.remoteConfig }
}

@Suppress("unused")
class MockAppContainer(context: Context) : SharedAppContainer(context) {
    override val profileRepository by lazy { MockProfileRepository() }
    override val scheduleRepository by lazy { MockScheduleRepository() }
    override val networkCacheRepository by lazy { MockNetworkCacheRepository() }
}

@Suppress("unused")
class RemoteAppContainer(context: Context) : SharedAppContainer(context) {
    override val profileRepository by lazy { RemoteProfileRepository(this) }
    override val scheduleRepository by lazy { RemoteScheduleRepository(this) }
    override val networkCacheRepository by lazy { LocalNetworkCacheRepository(this) }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppContainer(application: Application): AppContainer {
        return RemoteAppContainer(application.applicationContext)
    }
}

val Context.appContainer
    get() =
        (this.applicationContext as ru.n08i40k.polytechnic.next.Application).container
