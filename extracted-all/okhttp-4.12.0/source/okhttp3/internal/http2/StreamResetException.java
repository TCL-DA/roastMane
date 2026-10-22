package okhttp3.internal.http2

import java.io.IOException

class StreamResetException(errorCode: ErrorCode) : IOException("stream was reset: " + errorCode) {
   public final val errorCode: ErrorCode

   init {
      this.errorCode = errorCode;
   }
}
