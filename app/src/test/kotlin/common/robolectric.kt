package common

import org.robolectric.RuntimeEnvironment
import java.io.InputStream

val robolectric = RuntimeEnvironment.application

fun Any.stream(name: String): InputStream =
    javaClass.classLoader.getResourceAsStream(name)