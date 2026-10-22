package okhttp3

import okio.ByteString

interface WebSocket {
   public abstract fun request(): Request {
   }

   public abstract fun queueSize(): Long {
   }

   public abstract fun send(text: String): Boolean {
   }

   public abstract fun send(bytes: ByteString): Boolean {
   }

   public abstract fun close(code: Int, reason: String?): Boolean {
   }

   public abstract fun cancel() {
   }

   fun interface Factory {
      public abstract fun newWebSocket(request: Request, listener: WebSocketListener): WebSocket {
      }
   }
}
