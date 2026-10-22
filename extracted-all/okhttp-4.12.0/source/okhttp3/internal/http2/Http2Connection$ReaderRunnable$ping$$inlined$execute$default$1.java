package okhttp3.internal.http2

import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.concurrent.Task

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$execute$1\n+ 2 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n*L\n1#1,218:1\n807#2,2:219\n*E\n"])
public class `Http2Connection$ReaderRunnable$ping$$inlined$execute$default$1` : Task {
   fun `Http2Connection$ReaderRunnable$ping$$inlined$execute$default$1`(
      `$name`: java.lang.String, `$cancelable`: Boolean, var3: Http2Connection, var4: Int, var5: Int
   ) {
      super(`$name`, `$cancelable`);
      this.this$0 = var3;
      this.$payload1$inlined = var4;
      this.$payload2$inlined = var5;
   }

   public override fun runOnce(): Long {
      this.this$0.writePing(true, this.$payload1$inlined, this.$payload2$inlined);
      return -1L;
   }
}
