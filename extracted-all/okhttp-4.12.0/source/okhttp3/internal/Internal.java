@file:JvmName(name = "Internal")

package okhttp3.internal

import javax.net.ssl.SSLSocket
import okhttp3.Cache
import okhttp3.ConnectionSpec
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.Headers.Builder

public fun parseCookie(currentTimeMillis: Long, url: HttpUrl, setCookie: String): Cookie? {
   return Cookie.Companion.parse$okhttp(currentTimeMillis, url, setCookie);
}

public fun cookieToString(cookie: Cookie, forObsoleteRfc2965: Boolean): String {
   return cookie.toString$okhttp(forObsoleteRfc2965);
}

public fun addHeaderLenient(builder: Builder, line: String): Builder {
   return builder.addLenient$okhttp(line);
}

public fun addHeaderLenient(builder: Builder, name: String, value: String): Builder {
   return builder.addLenient$okhttp(name, value);
}

public fun cacheGet(cache: Cache, request: Request): Response? {
   return cache.get$okhttp(request);
}

public fun applyConnectionSpec(connectionSpec: ConnectionSpec, sslSocket: SSLSocket, isFallback: Boolean) {
   connectionSpec.apply$okhttp(sslSocket, isFallback);
}

