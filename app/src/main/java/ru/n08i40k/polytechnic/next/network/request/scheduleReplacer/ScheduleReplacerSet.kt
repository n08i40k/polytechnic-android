package ru.n08i40k.polytechnic.next.network.request.scheduleReplacer

import com.android.volley.Response
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.network.request.AuthorizedMultipartRequest

// TODO: вернуть
@Suppress("unused")
class ScheduleReplacerSet(
    appContainer: AppContainer,
    private val fileName: String,
    private val fileData: ByteArray,
    private val fileType: String,
    private val listener: Response.Listener<Nothing>,
    errorListener: Response.ErrorListener?
) : AuthorizedMultipartRequest(
    appContainer,
    Method.POST,
    "v1/schedule-replacer/set",
    { listener.onResponse(null) },
    errorListener
) {
    override val byteData: Map<String, DataPart>
        get() = mapOf(
            Pair(
                "file",
                DataPart(fileName, fileData, fileType)
            )
        )
}