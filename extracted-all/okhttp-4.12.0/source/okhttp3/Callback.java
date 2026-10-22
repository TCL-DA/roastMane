package okhttp3

import java.io.IOException

interface Callback {
   public abstract fun onFailure(call: Call, e: IOException) {
   }

   @Throws(java/io/IOException::class)
   public abstract fun onResponse(call: Call, response: Response) {
   }
}
