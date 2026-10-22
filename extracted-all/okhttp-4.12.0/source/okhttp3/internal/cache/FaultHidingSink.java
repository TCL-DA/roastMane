package okhttp3.internal.cache

import java.io.IOException
import okio.Buffer
import okio.ForwardingSink
import okio.Sink

internal open class FaultHidingSink(delegate: Sink, onException: (IOException) -> Unit) : ForwardingSink(delegate) {
   private final var hasErrors: Boolean
   public final val onException: (IOException) -> Unit

   init {
      this.onException = onException;
   }

   public open fun write(source: Buffer, byteCount: Long) {
      if (this.hasErrors) {
         source.skip(byteCount);
      } else {
         try {
            super.write(source, byteCount);
         } catch (IOException var5) {
            this.hasErrors = true;
            this.onException.invoke(var5);
         }
      }
   }

   public open fun flush() {
      if (!this.hasErrors) {
         try {
            super.flush();
         } catch (IOException var2) {
            this.hasErrors = true;
            this.onException.invoke(var2);
         }
      }
   }

   public open fun close() {
      if (!this.hasErrors) {
         try {
            super.close();
         } catch (IOException var2) {
            this.hasErrors = true;
            this.onException.invoke(var2);
         }
      }
   }
}
