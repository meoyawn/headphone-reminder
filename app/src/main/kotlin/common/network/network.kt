package common.network

import java.net.URLEncoder

fun urlEncode(s: String): String =
    URLEncoder.encode(s, "utf-8")