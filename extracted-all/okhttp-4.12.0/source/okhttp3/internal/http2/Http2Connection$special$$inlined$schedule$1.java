package okhttp3.internal.http2

import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.concurrent.Task

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$schedule$2\n+ 2 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n*L\n1#1,218:1\n153#2,14:219\n*E\n"])
public class `Http2Connection$special$$inlined$schedule$1` : Task {
   fun `Http2Connection$special$$inlined$schedule$1`(`$name`: java.lang.String, var2: Http2Connection, var3: Long) {
      super(`$name`, false, 2, null);
      this.this$0 = var2;
      this.$pingIntervalNanos$inlined = var3;
   }

   public override fun runOnce(): Long {
      var var9: Boolean;
      synchronized (this.this$0) {
         if (Http2Connection.access$getIntervalPongsReceived$p(this.this$0) < Http2Connection.access$getIntervalPingsSent$p(this.this$0)) {
            var9 = true;
         } else {
            Http2Connection.access$setIntervalPingsSent$p(this.this$0, Http2Connection.access$getIntervalPingsSent$p(this.this$0) + 1L);
            var9 = false;
         }

         var9 = var9;
      }

      var var10: Long;
      if (var9) {
         Http2Connection.access$failConnection(this.this$0, null);
         var10 = -1L;
      } else {
         this.this$0.writePing(false, 1, 0);
         var10 = this.$pingIntervalNanos$inlined;
      }

      return var10;
   }
}
