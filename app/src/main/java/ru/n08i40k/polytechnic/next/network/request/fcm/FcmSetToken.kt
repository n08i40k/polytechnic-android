package ru.n08i40k.polytechnic.next.network.request.fcm

import com.android.volley.Response
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class FcmSetToken(
    appContainer: AppContainer,
    token: String,
    listener: Response.Listener<Unit>,
    errorListener: Response.ErrorListener?,
) : AuthorizedRequest(
    appContainer,
    Method.PATCH,
    "v1/fcm/set-token?token=$token",
    { listener.onResponse(Unit) },
    errorListener,
    true
) {
    override fun getHeaders(): MutableMap<String, String> {
        val headers = super.getHeaders()
        headers.remove("Content-Type")

        return headers
    }
}