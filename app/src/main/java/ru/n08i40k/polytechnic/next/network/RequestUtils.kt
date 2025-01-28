package ru.n08i40k.polytechnic.next.network

import android.content.Context
import com.android.volley.VolleyError
import com.android.volley.toolbox.RequestFuture
import ru.n08i40k.polytechnic.next.utils.MyResult
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

fun <ResultT, RequestT : RequestBase> tryFuture(
    context: Context,
    buildRequest: (RequestFuture<ResultT>) -> RequestT
): MyResult<ResultT> {
    val future = RequestFuture.newFuture<ResultT>()

    buildRequest(future).send(context)

    return tryGet(future)
}

fun <T> tryGet(future: RequestFuture<T>): MyResult<T> {
    return try {
        MyResult.Success(future.get())
    } catch (exception: VolleyError) {
        MyResult.Failure(exception)
    } catch (exception: ExecutionException) {
        MyResult.Failure(exception.cause as VolleyError)
    } catch (exception: TimeoutException) {
        MyResult.Failure(exception)
    }
}

fun unwrapException(exception: Exception): Throwable {
    if (exception is ExecutionException && exception.cause != null)
        return exception.cause!!

    return exception
}