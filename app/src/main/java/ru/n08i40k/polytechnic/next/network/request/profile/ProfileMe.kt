package ru.n08i40k.polytechnic.next.network.request.profile

import com.android.volley.Response
import kotlinx.serialization.json.Json
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.request.AuthorizedRequest

class ProfileMe(
    appContainer: AppContainer,
    listener: Response.Listener<Profile>,
    errorListener: Response.ErrorListener?
) : AuthorizedRequest(
    appContainer,
    Method.GET,
    "v1/users/me",
    { listener.onResponse(Json.decodeFromString(it)) },
    errorListener
)