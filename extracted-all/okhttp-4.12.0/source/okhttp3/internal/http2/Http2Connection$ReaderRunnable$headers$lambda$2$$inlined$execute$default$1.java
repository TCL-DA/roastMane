package okhttp3.internal.http2

import java.io.IOException
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.concurrent.Task
import okhttp3.internal.platform.Platform

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$execute$1\n+ 2 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n+ 3 Util.kt\nokhttp3/internal/Util\n*L\n1#1,218:1\n688#2,5:219\n693#2,2:226\n696#2:231\n402#3,2:224\n404#3,3:228\n*S KotlinDebug\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n*L\n692#1:224,2\n692#1:228,3\n*E\n"])
public class `Http2Connection$ReaderRunnable$headers$lambda$2$$inlined$execute$default$1` : Task {
   fun `Http2Connection$ReaderRunnable$headers$lambda$2$$inlined$execute$default$1`(
      `$name`: java.lang.String, `$cancelable`: Boolean, var3: Http2Connection, var4: Http2Stream
   ) {
      super(`$name`, `$cancelable`);
      this.this$0 = var3;
      this.$newStream$inlined = var4;
   }

   public override fun runOnce(): Long {
      try {
         this.this$0.getListener$okhttp().onStream(this.$newStream$inlined);
      } catch (IOException var7) {
         var e: IOException = var7;
         Platform.Companion.get().log("Http2Connection.Listener failure for " + this.this$0.getConnectionName$okhttp(), 4, var7);

         try {
            this.$newStream$inlined.close(ErrorCode.PROTOCOL_ERROR, e);
         } catch (IOException var6) {
         }
      }

      return -1L;
   }
}
