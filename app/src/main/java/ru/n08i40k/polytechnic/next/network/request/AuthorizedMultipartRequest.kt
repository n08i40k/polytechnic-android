package ru.n08i40k.polytechnic.next.network.request

import com.android.volley.Response
import ru.n08i40k.polytechnic.next.app.AppContainer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import kotlin.math.min

open class AuthorizedMultipartRequest(
    appContainer: AppContainer,
    method: Int,
    url: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?,
    canBeUnauthorized: Boolean = false
) : AuthorizedRequest(appContainer, method, url, listener, errorListener, canBeUnauthorized) {
    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-" + System.currentTimeMillis()

    protected open val byteData: Map<String, DataPart>? get() = null

    override fun getBodyContentType(): String {
        return "multipart/form-data;boundary=$boundary"
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = super.getHeaders()
        headers["Content-Type"] = bodyContentType

        return headers
    }

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        try {
            val params = params
            if (!params.isNullOrEmpty()) {
                textParse(dos, params, paramsEncoding)
            }

            val data = byteData
            if (!data.isNullOrEmpty()) {
                dataParse(dos, data)
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            return bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    @Throws(IOException::class)
    private fun textParse(
        dataOutputStream: DataOutputStream, params: Map<String, String>, encoding: String
    ) {
        try {
            for ((key, value) in params) {
                buildTextPart(dataOutputStream, key, value)
            }
        } catch (uee: UnsupportedEncodingException) {
            throw RuntimeException("Encoding not supported: $encoding", uee)
        }
    }

    @Throws(IOException::class)
    private fun dataParse(dataOutputStream: DataOutputStream, data: Map<String, DataPart>) {
        for ((key, value) in data) {
            buildDataPart(dataOutputStream, value, key)
        }
    }

    @Throws(IOException::class)
    private fun buildTextPart(
        dataOutputStream: DataOutputStream, parameterName: String, parameterValue: String
    ) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"$lineEnd")
        dataOutputStream.writeBytes(lineEnd)
        dataOutputStream.writeBytes(parameterValue + lineEnd)
    }

    @Throws(IOException::class)
    private fun buildDataPart(
        dataOutputStream: DataOutputStream, dataFile: DataPart, inputName: String
    ) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes(
            "Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + dataFile.fileName + "\"" + lineEnd
        )

        if (dataFile.type != null && dataFile.type!!.trim { it <= ' ' }
                .isNotEmpty()) dataOutputStream.writeBytes("Content-Type: " + dataFile.type + lineEnd)

        dataOutputStream.writeBytes(lineEnd)

        val fileInputStream = ByteArrayInputStream(dataFile.content)
        var bytesAvailable = fileInputStream.available()

        val maxBufferSize = 1024 * 1024
        var bufferSize = min(bytesAvailable.toDouble(), maxBufferSize.toDouble()).toInt()
        val buffer = ByteArray(bufferSize)

        var bytesRead = fileInputStream.read(buffer, 0, bufferSize)

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize)
            bytesAvailable = fileInputStream.available()
            bufferSize = min(bytesAvailable.toDouble(), maxBufferSize.toDouble()).toInt()
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        }

        dataOutputStream.writeBytes(lineEnd)
    }

    inner class DataPart(name: String?, data: ByteArray, mimeType: String? = null) {
        var fileName: String? = name
        var content: ByteArray = data
        var type: String? = mimeType
    }
}