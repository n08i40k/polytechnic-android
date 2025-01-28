package ru.n08i40k.polytechnic.next.network.request

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.network.RequestBase
import ru.n08i40k.polytechnic.next.settings.settings

open class AuthorizedRequest(
    val appContainer: AppContainer,
    method: Int,
    url: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?,
    private val canBeUnauthorized: Boolean = false,
) : RequestBase(
    method,
    url,
    listener,
    @Singleton
    object : Response.ErrorListener {
        override fun onErrorResponse(error: VolleyError?) {
            val context = appContainer.context

            if (!canBeUnauthorized && error is AuthFailureError) {
                runBlocking {
                    context.settings.updateData { currentSettings ->
                        currentSettings
                            .toBuilder()
                            .clear()
                            .build()
                    }
                }

                // TODO: если не авторизован
//            if (context.profileViewModel != null)
//                context.profileViewModel!!.onUnauthorized()
            }

            runBlocking { appContainer.profileRepository.signOut() }

            errorListener?.onErrorResponse(error)

        }
    }) {

    override fun getHeaders(): MutableMap<String, String> {
        val accessToken = runBlocking {
            appContainer.context
                .settings
                .data
                .map { settings -> settings.accessToken }
                .first()
        }

        // TODO: если не авторизован
//        if (accessToken.isEmpty() && context.profileViewModel != null)
//            context.profileViewModel!!.onUnauthorized()

        val headers = super.getHeaders()
        headers["Authorization"] = "Bearer $accessToken"

        return headers
    }

    val appContext get() = appContainer.context
}