package okhttp3.internal.http

import java.net.Proxy.Type
import okhttp3.HttpUrl
import okhttp3.Request

object RequestLine private constructor() {
   @JvmStatic
   public RequestLine INSTANCE = new RequestLine();


   public fun get(request: Request, proxyType: Type): String {
      var var3: StringBuilder = new StringBuilder();
      var3.append(request.method());
      var3.append(' ');
      if (INSTANCE.includeAuthorityInRequestLine(request, proxyType)) {
         var3.append(request.url());
      } else {
         var3.append(INSTANCE.requestPath(request.url()));
      }

      var3.append(" HTTP/1.1");
      var var10000: java.lang.String = var3.toString();
      return var10000;
   }

   private fun includeAuthorityInRequestLine(request: Request, proxyType: Type): Boolean {
      return !request.isHttps() && proxyType === Type.HTTP;
   }

   public fun requestPath(url: HttpUrl): String {
      var path: java.lang.String = url.encodedPath();
      var query: java.lang.String = url.encodedQuery();
      return if (query != null) path + '?' + query else path;
   }
}
