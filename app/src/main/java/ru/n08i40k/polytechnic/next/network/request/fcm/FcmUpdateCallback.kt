package ru.n08i40k.polytechnic.next.network.request.fcm

import com.android.volley.Response
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

// TODO: вернуть
@Suppress("unused")
class FcmUpdateCallback(
    appContainer: AppContainer,
    version: String,
    listener: Response.Listener<Unit>,
    errorListener: Response.ErrorListener?,
) : AuthorizedRequest(
    appContainer,
    Method.POST,
    "v1/fcm/update-callback/$version",
    { listener.onResponse(Unit) },
    errorListener,
    true
)