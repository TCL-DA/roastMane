package okhttp3.internal.http1

import okhttp3.Headers
import okio.BufferedSource

class HeadersReader(source: BufferedSource) {
   @JvmStatic
   public HeadersReader.Companion Companion = new HeadersReader.Companion(null);
   @JvmStatic
   private int HEADER_LIMIT = 262144;

   private final var headerLimit: Long
   public final val source: BufferedSource

   init {
      this.source = source;
      this.headerLimit = 262144L;
   }

   public fun readLine(): String {
      var line: java.lang.String = this.source.readUtf8LineStrict(this.headerLimit);
      this.headerLimit = this.headerLimit - (long)line.length();
      return line;
   }

   public fun readHeaders(): Headers {
      var result: Headers.Builder = new Headers.Builder();

      while (true) {
         var line: java.lang.String = this.readLine();
         if (line.length() == 0) {
            return result.build();
         }

         result.addLenient$okhttp(line);
      }
   }

   companion object Companion private constructor() {
      private const val HEADER_LIMIT: Int

   }
}
