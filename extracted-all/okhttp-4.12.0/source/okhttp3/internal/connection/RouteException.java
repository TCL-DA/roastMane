package okhttp3.internal.connection

import java.io.IOException

class RouteException internal constructor(firstConnectException: IOException) : RuntimeException(firstConnectException) {
   public final val firstConnectException: IOException
   public final var lastConnectException: IOException
      private set

   init {
      this.firstConnectException = firstConnectException;
      this.lastConnectException = this.firstConnectException;
   }

   public fun addConnectException(e: IOException) {
      ExceptionsKt.addSuppressed(this.firstConnectException, e);
      this.lastConnectException = e;
   }
}
