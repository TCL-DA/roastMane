package okhttp3

import okio.Timeout

interface Call : Cloneable {
   public abstract fun request(): Request {
   }

   @Throws(java/io/IOException::class)
   public abstract fun execute(): Response {
   }

   public abstract fun enqueue(responseCallback: Callback) {
   }

   public abstract fun cancel() {
   }

   public abstract fun isExecuted(): Boolean {
   }

   public abstract fun isCanceled(): Boolean {
   }

   public abstract fun timeout(): Timeout {
   }

   public abstract fun clone(): Call {
   }

   fun interface Factory {
      public abstract fun newCall(request: Request): Call {
      }
   }
}
