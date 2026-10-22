package okhttp3.internal.http2

import okio.BufferedSource

interface PushObserver {
   @JvmStatic
   PushObserver.Companion Companion = PushObserver.Companion.$$INSTANCE;
   @JvmField
   @JvmStatic
   PushObserver CANCEL = new PushObserver.Companion.PushObserverCancel();

   public abstract fun onRequest(streamId: Int, requestHeaders: List<Header>): Boolean {
   }

   public abstract fun onHeaders(streamId: Int, responseHeaders: List<Header>, last: Boolean): Boolean {
   }

   @Throws(java/io/IOException::class)
   public abstract fun onData(streamId: Int, source: BufferedSource, byteCount: Int, last: Boolean): Boolean {
   }

   public abstract fun onReset(streamId: Int, errorCode: ErrorCode) {
   }

   companion object Companion private constructor() {
      public final val CANCEL: PushObserver


      private class PushObserverCancel : PushObserver {
         public override fun onRequest(streamId: Int, requestHeaders: List<Header>): Boolean {
            return true;
         }

         public override fun onHeaders(streamId: Int, responseHeaders: List<Header>, last: Boolean): Boolean {
            return true;
         }

         @Throws(java/io/IOException::class)
         public override fun onData(streamId: Int, source: BufferedSource, byteCount: Int, last: Boolean): Boolean {
            source.skip((long)byteCount);
            return true;
         }

         public override fun onReset(streamId: Int, errorCode: ErrorCode) {
         }
      }
   }
}
