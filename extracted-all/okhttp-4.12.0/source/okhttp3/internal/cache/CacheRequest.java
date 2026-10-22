package okhttp3.internal.cache

import okio.Sink

interface CacheRequest {
   @Throws(java/io/IOException::class)
   public abstract fun body(): Sink {
   }

   public abstract fun abort() {
   }
}
