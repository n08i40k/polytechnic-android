package ru.n08i40k.polytechnic.next.network

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.util.logging.Logger

open class RequestBase(
    method: Int,
    url: String?,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?
) : StringRequest(method, NetworkValues.API_HOST + url, listener, errorListener) {
    open fun send(context: Context) {
        Logger.getLogger("RequestBase").info("Sending request to $url")
        NetworkConnection.getInstance(context).addToRequestQueue(this)
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = mutableMapOf<String, String>()
        headers["Content-Type"] = "application/json; charset=utf-8"

        return headers
    }
}