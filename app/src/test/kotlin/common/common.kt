package common

import android.app.Application
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import okio.Okio
import org.robolectric.RuntimeEnvironment

fun Any.file(name: String): String =
    Okio.buffer(Okio.source(javaClass.classLoader.getResourceAsStream(name))).use { it.readUtf8() }

fun MockWebServer.enqueueResource(name: String): Unit =
    enqueue(MockResponse().setBody(file(name)))

fun robolectric(): Application =
    RuntimeEnvironment.application