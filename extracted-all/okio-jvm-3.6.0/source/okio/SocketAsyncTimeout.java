package okio

import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.logging.Level

private class SocketAsyncTimeout(socket: Socket) : AsyncTimeout {
   private final val socket: Socket

   init {
      this.socket = socket;
   }

   protected override fun newTimeoutException(cause: IOException?): IOException {
      var ioe: SocketTimeoutException = new SocketTimeoutException("timeout");
      if (cause != null) {
         ioe.initCause(cause);
      }

      return ioe;
   }

   protected override fun timedOut() {
      try {
         this.socket.close();
      } catch (Exception var2) {
         Okio__JvmOkioKt.access$getLogger$p().log(Level.WARNING, "Failed to close timed out socket " + this.socket, var2);
      } catch (AssertionError var3) {
         if (!Okio.isAndroidGetsocknameError(var3)) {
            throw var3;
         }

         Okio__JvmOkioKt.access$getLogger$p().log(Level.WARNING, "Failed to close timed out socket " + this.socket, var3);
      }
   }
}
