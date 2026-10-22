package okhttp3.internal.http2

import java.io.IOException
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.concurrent.Task

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$execute$1\n+ 2 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n*L\n1#1,218:1\n341#2,6:219\n*E\n"])
public class `Http2Connection$writeSynResetLater$$inlined$execute$default$1` : Task {
   fun `Http2Connection$writeSynResetLater$$inlined$execute$default$1`(
      `$name`: java.lang.String, `$cancelable`: Boolean, var3: Http2Connection, var4: Int, var5: ErrorCode
   ) {
      super(`$name`, `$cancelable`);
      this.this$0 = var3;
      this.$streamId$inlined = var4;
      this.$errorCode$inlined = var5;
   }

   public override fun runOnce(): Long {
      try {
         this.this$0.writeSynReset$okhttp(this.$streamId$inlined, this.$errorCode$inlined);
      } catch (IOException var3) {
         Http2Connection.access$failConnection(this.this$0, var3);
      }

      return -1L;
   }
}
