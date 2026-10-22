package okhttp3.internal.http

object HttpMethod private constructor() {
   @JvmStatic
   public HttpMethod INSTANCE = new HttpMethod();


   public fun invalidatesCache(method: String): Boolean {
      return method == "POST" || method == "PATCH" || method == "PUT" || method == "DELETE" || method == "MOVE";
   }

   @JvmStatic
   public fun requiresRequestBody(method: String): Boolean {
      return method == "POST" || method == "PUT" || method == "PATCH" || method == "PROPPATCH" || method == "REPORT";
   }

   @JvmStatic
   public fun permitsRequestBody(method: String): Boolean {
      return !(method == "GET") && !(method == "HEAD");
   }

   public fun redirectsWithBody(method: String): Boolean {
      return method == "PROPFIND";
   }

   public fun redirectsToGet(method: String): Boolean {
      return !(method == "PROPFIND");
   }
}
