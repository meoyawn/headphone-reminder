package common.http

import com.squareup.okhttp.Callback
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import common.rx.error
import common.rx.finally
import common.rx.success
import rx.Single
import rx.SingleSubscriber
import java.io.IOException

fun request(url: HttpUrl): Request =
    Request.Builder().url(url).build()

fun call(client: OkHttpClient, req: Request): Single<Response> =
    Single.create(onSubscribe(client, req))

private fun onSubscribe(client: OkHttpClient, req: Request): (SingleSubscriber<in Response>) -> Unit =
    { ss ->
      val call = client.newCall(req)
      ss.finally { call.cancel() }
      call.enqueue(callback(ss))
    }

private fun callback(ss: SingleSubscriber<in Response>): Callback =
    object : Callback {
      override fun onFailure(request: Request, e: IOException): Unit = ss.error(e)
      override fun onResponse(response: Response): Unit = ss.success(response)
    }