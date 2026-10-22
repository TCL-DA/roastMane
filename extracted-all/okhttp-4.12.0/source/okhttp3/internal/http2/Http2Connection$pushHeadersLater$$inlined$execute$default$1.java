package okhttp3.internal.http2

import java.io.IOException
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.concurrent.Task

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$execute$1\n+ 2 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n+ 3 Util.kt\nokhttp3/internal/Util\n*L\n1#1,218:1\n912#2,2:219\n914#2,7:223\n921#2:233\n402#3,2:221\n404#3,3:230\n*S KotlinDebug\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n*L\n913#1:221,2\n913#1:230,3\n*E\n"])
public class `Http2Connection$pushHeadersLater$$inlined$execute$default$1` : Task {
   fun `Http2Connection$pushHeadersLater$$inlined$execute$default$1`(
      `$name`: java.lang.String, `$cancelable`: Boolean, var3: Http2Connection, var4: Int, var5: java.util.List, var6: Boolean
   ) {
      super(`$name`, `$cancelable`);
      this.this$0 = var3;
      this.$streamId$inlined = var4;
      this.$requestHeaders$inlined = var5;
      this.$inFinished$inlined = var6;
   }

   public override fun runOnce(): Long {
      var cancel: Boolean = Http2Connection.access$getPushObserver$p(this.this$0)
         .onHeaders(this.$streamId$inlined, this.$requestHeaders$inlined, this.$inFinished$inlined);

      try {
         if (cancel) {
            this.this$0.getWriter().rstStream(this.$streamId$inlined, ErrorCode.CANCEL);
         }

         if (cancel || this.$inFinished$inlined) {
            synchronized (this.this$0) {
               var var10: Boolean = Http2Connection.access$getCurrentPushRequests$p(this.this$0).remove(this.$streamId$inlined);
            }
         }
      } catch (IOException var9) {
      }

      return -1L;
   }
}
