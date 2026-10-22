package okhttp3

interface CookieJar {
   @JvmStatic
   CookieJar.Companion Companion = CookieJar.Companion.$$INSTANCE;
   @JvmField
   @JvmStatic
   CookieJar NO_COOKIES = new CookieJar.Companion.NoCookies();

   public abstract fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
   }

   public abstract fun loadForRequest(url: HttpUrl): List<Cookie> {
   }

   companion object Companion private constructor() {
      public final val NO_COOKIES: CookieJar


      private class NoCookies : CookieJar {
         public override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
         }

         public override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return CollectionsKt.emptyList();
         }
      }
   }
}
