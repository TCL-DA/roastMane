package okhttp3

object OkHttp private constructor() {
   @JvmStatic
   public OkHttp INSTANCE = new OkHttp();

   public const val VERSION: String = "4.12.0"

}
